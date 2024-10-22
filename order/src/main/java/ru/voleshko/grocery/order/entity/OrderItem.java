package ru.voleshko.grocery.order.entity;

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
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class OrderItem {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private UUID productId;

    private String productName;

    private BigDecimal productPrice;

    private int qty;

    @ManyToOne
    @JoinColumn(name="order_id")
    @JsonIgnore
    private Order order;

}
