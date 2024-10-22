package ru.voleshko.grocery.product.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
public class Price {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private ZonedDateTime startDate;

    @Column(name = "value", precision = 7, scale = 2)
    private BigDecimal value;

    @ManyToOne
    @JoinColumn(name="product_id")
    private Product product;
}
