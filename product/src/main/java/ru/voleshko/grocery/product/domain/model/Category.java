package ru.voleshko.grocery.product.domain.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "category_name_idx")
})
public class Category {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private String name;

    private String description;

    private boolean isLeaf;

    @OneToOne(fetch = FetchType.LAZY)
    private Category parentCategory;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "category_attribute",
            joinColumns = @JoinColumn(name = "category_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_id", referencedColumnName = "id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"category_id", "attribute_id"})
    )
    private List<Attribute> attributes;
}
