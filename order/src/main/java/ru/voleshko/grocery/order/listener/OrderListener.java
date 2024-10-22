package ru.voleshko.grocery.order.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.order.OrderService;
import ru.voleshko.grocery.order.entity.Order;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderListener {

    private final OrderService orderService;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "CHANGE_ORDER_STATUS")
    @RabbitListener(queues = "changeOrderStatusQueue")
    public void changeOrderStatus(String changeOrderStatusDto,
                                  @Header("Idempotency-key") UUID idempotencyKey
    ) {

        ChangeOrderStatusDto dto = objectMapper.readValue(changeOrderStatusDto, ChangeOrderStatusDto.class);
        orderService.changeOrderStatus(dto.getOrderId(), dto.getStatus());
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class ChangeOrderStatusDto {

        private UUID orderId;
        private Order.OrderStatus status;
    }

}
