package ru.voleshko.grocery.product.domain.model;

import lombok.*;
import org.springframework.lang.Nullable;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "product", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "product_name_idx")
})
public class Product {

    @Id
    private UUID id;

    private String name;

    @Nullable
    private String description;

    @Nullable
    private Float weight;

    @Nullable
    private Byte discountPercent;

    @Nullable
    private String attributes;

    @Column(name = "price", precision = 7, scale = 2)
    private BigDecimal price;

    private int qty;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_product",
            joinColumns = @JoinColumn(name = "product_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "product_id"})
    )
    private List<Category> categories;
}
