package ru.voleshko.grocery.shipment.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
public class Shipment {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ShipmentStatus status = ShipmentStatus.CREATED;

    private String address;

    private UUID orderId;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private ZonedDateTime shipmentFrom;

    private ZonedDateTime shipmentTo;

    @OneToMany(mappedBy = "shipment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ShipmentItem> shipmentItems;

    public Shipment(String address, UUID orderId, ZonedDateTime shipmentFrom, ZonedDateTime shipmentTo) {
        this.status = ShipmentStatus.CREATED;
        this.address = address;
        this.orderId = orderId;
        this.createdAt = ZonedDateTime.now();
        this.shipmentFrom = shipmentFrom;
        this.shipmentTo = shipmentTo;
        shipmentItems = new ArrayList<>();
    }

    public Shipment() {
    }

    public enum ShipmentStatus {
        CREATED, CANCELED, DONE
    }
}
