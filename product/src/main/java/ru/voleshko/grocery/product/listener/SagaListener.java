package ru.voleshko.grocery.product.listener;

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
import ru.voleshko.grocery.product.domain.model.Reservation;
import ru.voleshko.grocery.product.rest.v1.converter.reservation.ReservationSaveConverter;
import ru.voleshko.grocery.product.rest.v1.dto.reservation.ReservationSaveDto;
import ru.voleshko.grocery.product.service.ReservationService;
import ru.voleshko.lib.idempotency.Idempotent;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.UUID;

@Slf4j
@Component
public class SagaListener {

    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ReservationService reservationService;
    private final ReservationSaveConverter reservationConverter;
    private final SagaListener self;

    public SagaListener(
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper,
            ReservationService reservationService,
            ReservationSaveConverter reservationConverter,
            @Lazy SagaListener self
    ) {
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
        this.reservationService = reservationService;
        this.reservationConverter = reservationConverter;
        this.self = self;
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "RESERVATION")
    @RabbitListener(queues = "reservationRequestQueue")
    public void requestReservation(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        ReservationSaveDto dto = objectMapper.readValue(order, ReservationSaveDto.class);

        try {
            Reservation reservation = reservationConverter.toDomain(dto);
            reservation.getReservationItems().forEach(item -> item.setReservation(reservation));
            reservationService.save(reservation);

            outboxRepository.save(new OutboxMessage(
                    "reservationExchange",
                    "reservationSuccessQueue",
                    order
            ));
        } catch (Exception exception) {
            log.error("Failed to make reservation for order: ",  exception);
            self.handleException(order);
        }
    }

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "RELEASE_RESERVATION")
    @RabbitListener(queues = "reservationRollbackQueue")
    public void rollbackReservation(String order, @Header("Idempotency-key") UUID idempotencyKey) {
        JsonNode parsedOrder = objectMapper.readTree(order);
        UUID orderId = UUID.fromString(parsedOrder.get("id").asText());

        reservationService.cancelReservation(orderId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void handleException(String order) {
        outboxRepository.save(new OutboxMessage(
                "reservationExchange",
                "reservationFailQueue",
                order
        ));
    }

}
