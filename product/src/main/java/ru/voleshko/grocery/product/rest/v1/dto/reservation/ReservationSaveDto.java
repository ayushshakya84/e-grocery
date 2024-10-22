package ru.voleshko.grocery.product.rest.v1.dto.reservation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ReservationSaveDto {

    @JsonProperty("id")
    private UUID orderId;

    @JsonProperty("orderItems")
    private List<ReservationItemSaveDto> reservationItems;

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public static class ReservationItemSaveDto {

        private int qty;

        private UUID productId;
    }
}
