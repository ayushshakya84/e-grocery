package ru.voleshko.grocery.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.grocery.product.domain.model.Reservation;

import java.util.Optional;
import java.util.UUID;

public interface ReservationRepository extends
        JpaRepository<Reservation, UUID> {

    @Query(value = "SELECT * FROM reservation WHERE order_id = :orderId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Reservation> getLatestReservationForOrder(UUID orderId);
}
