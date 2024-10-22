package ru.voleshko.grocery.order.dto;

import lombok.*;
import ru.voleshko.grocery.order.entity.OrderItem;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class OrderDto {

    private UUID id;

    private BigDecimal totalPrice;

    private String address;

    private ZonedDateTime shipmentFrom;

    private ZonedDateTime shipmentTo;

    private String status;

    private List<OrderItem> orderItems;

    private String cardNumber;

    private String cardValidTo;

    private String cardCvv;

}
