package ru.voleshko.grocery.payment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.grocery.payment.entity.Payment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {

    @Query(value = "SELECT * FROM payment WHERE order_id = :orderId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Payment> findLatestPaymentForOrder(UUID orderId);

    List<Payment> findAllByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
