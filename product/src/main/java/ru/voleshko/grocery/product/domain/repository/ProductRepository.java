package ru.voleshko.grocery.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.voleshko.grocery.product.domain.model.Product;

import java.util.UUID;

public interface ProductRepository extends
        JpaRepository<Product, UUID>,
        DefaultQueryDslRepository<Product> {

}
