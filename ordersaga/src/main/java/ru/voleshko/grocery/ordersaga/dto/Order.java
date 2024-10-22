package ru.voleshko.grocery.ordersaga.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Order {

    private UUID id;

    private BigDecimal totalPrice;

    private String address;

    private ZonedDateTime shipmentFrom;

    private ZonedDateTime shipmentTo;

    private OrderStatus status;

    private List<OrderItem> orderItems;

    private String cardNumber;

    private String cardValidTo;

    private String cardCvv;

    public enum OrderStatus {
        CREATED, PROCESSING, ERROR, SUCCESS
    }
}
