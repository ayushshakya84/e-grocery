package ru.voleshko.grocery.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.grocery.product.domain.model.Price;

import java.util.List;
import java.util.UUID;

public interface PriceRepository extends
        JpaRepository<Price, UUID>,
        DefaultQueryDslRepository<Price> {

    @Query(value = "select * from price p where p.product_id = :productId order by p.start_date asc", nativeQuery = true)
    List<Price> findAllByProduct(UUID productId);
}
