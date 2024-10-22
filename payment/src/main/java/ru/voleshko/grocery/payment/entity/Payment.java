package ru.voleshko.grocery.payment.entity;

import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class Payment {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    private BigDecimal amount;

    private String cardNumber;

    private String cardValidTo;

    private UUID orderId;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    public enum PaymentStatus {
        SUCCESS, FAIL, REFUNDED
    }
}
