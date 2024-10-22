package ru.voleshko.grocery.product.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Product;
import ru.voleshko.grocery.product.domain.model.Reservation;
import ru.voleshko.grocery.product.domain.model.ReservationItem;
import ru.voleshko.grocery.product.domain.repository.ReservationRepository;
import ru.voleshko.grocery.product.rest.v1.converter.product.ProductConverter;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final ProductService productService;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ProductConverter productConverter;

    @SneakyThrows
    public void save(Reservation reservation) {
        reservationRepository.save(reservation);

        for (ReservationItem item : reservation.getReservationItems()) {
            Product product = productService.findById(item.getProduct().getId());
            int remainingQty = product.getQty() - item.getQty();
            if (remainingQty < 0) {
                throw new IllegalStateException("Can not make reservation. Not enough " + product.getName() + "s available");
            }
            product.setQty(remainingQty);
            outboxRepository.save(new OutboxMessage(
                    "productExchange",
                    "productUpsertedQueue",
                    objectMapper.writeValueAsString(productConverter.fromDomain(product)))
            );
        }
    }

    @Transactional
    public void cancelReservation(UUID orderId) {
        Reservation reservation = reservationRepository.getLatestReservationForOrder(orderId)
                .orElseThrow(() -> new IllegalStateException("No reservations found for order id=[" + orderId + "]"));

        reservation.getReservationItems().forEach(item -> {
            productService
                    .findByIdOptionally(item.getProduct().getId())
                    .ifPresent(product -> {
                        int updatedQty = product.getQty() + item.getQty();
                        product.setQty(updatedQty);
                        try {
                            outboxRepository.save(new OutboxMessage(
                                    "productExchange",
                                    "productUpsertedQueue",
                                    objectMapper.writeValueAsString(productConverter.fromDomain(product)))
                            );
                        } catch (JsonProcessingException e) {
                            throw new RuntimeException(e);
                        }
                    });
        });
        reservation.setStatus(Reservation.ReservationStatus.CANCELED);
    }
}
