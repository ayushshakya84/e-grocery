package ru.voleshko.grocery.shipment.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ShipmentDto {

    private String address;

    @JsonProperty("id")
    private UUID orderId;

    private ZonedDateTime shipmentFrom;

    private ZonedDateTime shipmentTo;

    @JsonProperty("orderItems")
    private List<ShipmentItemDto> shipmentItems;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ShipmentItemDto {

        private int qty;

        private UUID productId;

        private String productName;
    }
}
