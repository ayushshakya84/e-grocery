package ru.voleshko.grocery.ordersaga.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class OrderItem {

    private UUID id;

    private UUID productId;

    private String productName;

    private BigDecimal productPrice;

    private int qty;

}
