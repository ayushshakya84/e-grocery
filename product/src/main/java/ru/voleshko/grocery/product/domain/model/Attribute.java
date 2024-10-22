package ru.voleshko.grocery.product.domain.model;

import lombok.*;

import javax.persistence.*;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
@Entity
@Table(name = "`attribute`", uniqueConstraints = {
        @UniqueConstraint(columnNames = "name", name = "attribute_name_idx")
})
public class Attribute {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private String name;

    @Enumerated(EnumType.STRING)
    private AttributeType type;

    public enum AttributeType {
        NUMBER, TEXT
    }

}
