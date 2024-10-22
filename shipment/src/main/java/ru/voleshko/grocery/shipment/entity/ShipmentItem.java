package ru.voleshko.grocery.shipment.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Table(name = "shipment_item")
public class ShipmentItem {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private int qty;

    private UUID productId;

    private String productName;

    @ManyToOne
    @JoinColumn(name="shipment_id")
    @JsonIgnore
    private Shipment shipment;

}
