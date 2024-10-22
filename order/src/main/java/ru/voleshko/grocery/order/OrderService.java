package ru.voleshko.grocery.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.order.entity.Order;
import ru.voleshko.grocery.order.repository.OrderRepository;
import ru.voleshko.lib.auth.User;
import ru.voleshko.lib.auth.UserService;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import javax.persistence.EntityNotFoundException;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.springframework.util.StringUtils.capitalize;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserService userService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    private final static DateTimeFormatter DTF = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);

    @Transactional(readOnly = true)
    public List<Order> findAll() {
        return orderRepository.findAllUserOrders(userService.getUserId());
    }

    @Transactional(readOnly = true)
    public Order findById(UUID orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("Order with id=" + orderId + " was not found"));
    }

    @Transactional
    public Order save(Order order) {
        order.getOrderItems().forEach(orderItem -> orderItem.setOrder(order));

        User user = userService.getUserAuthentication();
        order.setUserId(UUID.fromString(user.getId()));
        order.setUserName(user.getFirstName());
        order.setUserEmail(user.getEmail());

        return orderRepository.save(order);
    }

    public Order changeOrderStatus(UUID orderId, Order.OrderStatus newOrderStatus) {
        Order order = findById(orderId);
        saveOutboundNotification(order, newOrderStatus,
                new HashMap<>() {{
                    put("userName", order.getUserName());
                    put("shipmentFrom", DTF.format(order.getShipmentFrom()));
                    put("shipmentTo", DTF.format(order.getShipmentTo()));
                }});
        order.setStatus(newOrderStatus);
        return orderRepository.save(order);
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class NotificationDto {

        private String type;
        private String recipientEmail;
        private Map<String, String> notification;
    }

    @SneakyThrows
    private void saveOutboundNotification(Order order,
                                          Order.OrderStatus newStatus,
                                          Map<String, String> notificationPayload
    ) {
        String type = "order" + capitalize(newStatus.name().toLowerCase());
        outboxRepository.save(
                new OutboxMessage(
                        "notificationExchange",
                        "orderNotificationQueue",
                        objectMapper.writeValueAsString(
                                new NotificationDto(
                                        type,
                                        order.getUserEmail(),
                                        notificationPayload
                                )
                        ))
        );
    }
}
