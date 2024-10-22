package ru.voleshko.grocery.shipment.listener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.shipment.ShipmentService;
import ru.voleshko.grocery.shipment.dto.ShipmentConverter;
import ru.voleshko.grocery.shipment.dto.ShipmentDto;
import ru.voleshko.grocery.shipment.entity.Shipment;
import ru.voleshko.lib.idempotency.Idempotent;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.UUID;

@Slf4j
@Component
public class SagaListener {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ShipmentService shipmentService;
    private final ShipmentConverter shipmentConverter;
    private final SagaListener self;

    public SagaListener(OutboxRepository outboxRepository,
                        ObjectMapper objectMapper,
                        ShipmentService shipmentService,
                        ShipmentConverter shipmentConverter,
                        @Lazy SagaListener self
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.shipmentService = shipmentService;
        this.shipmentConverter = shipmentConverter;
        this.self = self;
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "SHIPMENT")
    @RabbitListener(queues = "shipmentRequestQueue")
    public void requestShipment(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        ShipmentDto shipmentDto = objectMapper.readValue(order, ShipmentDto.class);
        try {
            Shipment shipment = shipmentConverter.toDomain(shipmentDto);
            shipment.getShipmentItems().forEach(item -> item.setShipment(shipment));
            shipmentService.save(shipment);

            outboxRepository.save(new OutboxMessage(
                    "shipmentExchange",
                    "shipmentSuccessQueue",
                    order
            ));
        } catch (Exception exception) {
            log.error("Failed to create shipment for order with id = [{}]: ", shipmentDto.getOrderId(), exception);
            self.handleException(order);
        }
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "CANCEL_SHIPMENT")
    @RabbitListener(queues = "shipmentRollbackQueue")
    public void rollbackShipment(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        JsonNode parsedOrder = objectMapper.readTree(order);
        UUID orderId = UUID.fromString(parsedOrder.get("id").asText());

        shipmentService.cancelShipment(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleException(String order) {
        outboxRepository.save(new OutboxMessage(
                "shipmentExchange",
                "shipmentFailQueue",
                order
        ));
    }
}
