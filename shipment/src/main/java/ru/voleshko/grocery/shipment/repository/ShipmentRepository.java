package ru.voleshko.grocery.shipment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.grocery.shipment.entity.Shipment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ShipmentRepository extends JpaRepository<Shipment, UUID> {

    @Query(value = "SELECT * FROM shipment WHERE order_id = :orderId ORDER BY created_at DESC LIMIT 1", nativeQuery = true)
    Optional<Shipment> getLatestShipmentForOrder(UUID orderId);

    List<Shipment> findAllByOrderIdOrderByCreatedAtDesc(UUID orderId);
}
