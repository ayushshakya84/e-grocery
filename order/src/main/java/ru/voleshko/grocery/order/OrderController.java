package ru.voleshko.grocery.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import ru.voleshko.grocery.order.dto.OrderDto;
import ru.voleshko.grocery.order.entity.Order;
import ru.voleshko.lib.idempotency.Idempotent;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class OrderController {

    private final OrderService orderService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "CREATE_ORDER")
    @PostMapping("/orders")
    public Order save(
            @RequestBody Order order,
            @RequestHeader("Idempotency-key") UUID idempotencyKey
    ) {
        return orderService.save(order);
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "START_ORDER_PROCESSING")
    @PostMapping("/orders/start-order-processing")
    public void startOrderProcessing(
            @RequestBody OrderDto order,
            @RequestHeader("Idempotency-key") UUID idempotencyKey
    ) {
        orderService.changeOrderStatus(order.getId(), Order.OrderStatus.PROCESSING);
        outboxRepository.save(new OutboxMessage(
                "sagaExchange",
                "startSagaQueue",
                objectMapper.writeValueAsString(order)
        ));
    }

    @GetMapping("/orders")
    public List<Order> findAll() {
        return orderService.findAll();
    }

    @Transactional
    @PatchMapping("/orders/change-status")
    public void changeOrderStatus(@RequestParam UUID orderId, @RequestParam Order.OrderStatus status) {
        orderService.changeOrderStatus(orderId, status);
    }
}
