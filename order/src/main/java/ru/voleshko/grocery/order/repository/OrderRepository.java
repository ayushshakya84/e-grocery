package ru.voleshko.grocery.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.voleshko.grocery.order.entity.Order;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("select distinct o from Order o " +
            "join fetch o.orderItems " +
            "where o.userId = :userId " +
            "order by o.createdAt desc"
    )
    List<Order> findAllUserOrders(@Param(value = "userId") UUID userId);
}
