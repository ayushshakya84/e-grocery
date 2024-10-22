package ru.voleshko.grocery.shipment;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.shipment.entity.Shipment;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1")
public class ShipmentController {

    private final ShipmentService shipmentService;

    @GetMapping("/shipments/{orderId}")
    public List<Shipment> findByOrderId(@PathVariable UUID orderId) {
        return shipmentService.findByOrderId(orderId);
    }
}
