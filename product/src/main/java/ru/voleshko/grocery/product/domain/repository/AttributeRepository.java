package ru.voleshko.grocery.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.voleshko.grocery.product.domain.model.Attribute;

import java.util.UUID;

public interface AttributeRepository extends
        JpaRepository<Attribute, UUID>,
        DefaultQueryDslRepository<Attribute> {

}
