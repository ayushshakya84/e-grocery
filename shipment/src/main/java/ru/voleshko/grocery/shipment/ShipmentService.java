package ru.voleshko.grocery.shipment;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.shipment.entity.Shipment;
import ru.voleshko.grocery.shipment.repository.ShipmentRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    @Transactional(readOnly = true)
    public List<Shipment> findByOrderId(UUID orderId) {
        return shipmentRepository.findAllByOrderIdOrderByCreatedAtDesc(orderId);
    }

    public Shipment save(Shipment shipment) {
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public void cancelShipment(UUID orderId) {
        Shipment shipment = shipmentRepository.getLatestShipmentForOrder(orderId)
                .orElseThrow(() -> new IllegalStateException("No shipments found for order id=[" + orderId + "]"));
        shipment.setStatus(Shipment.ShipmentStatus.CANCELED);
    }
}
