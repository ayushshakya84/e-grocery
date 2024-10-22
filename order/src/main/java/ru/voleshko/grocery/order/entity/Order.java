package ru.voleshko.grocery.order.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
@Table(name = "`order`")
public class Order {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private BigDecimal totalPrice;

    private String address;

    private ZonedDateTime shipmentFrom;

    private ZonedDateTime shipmentTo;

    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.CREATED;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderItem> orderItems;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    private UUID userId;

    private String userName;

    private String userEmail;

    public enum OrderStatus {
        CREATED, PROCESSING, ERROR, SUCCESS
    }
}
