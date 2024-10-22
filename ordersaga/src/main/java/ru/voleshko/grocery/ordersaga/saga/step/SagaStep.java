package ru.voleshko.grocery.ordersaga.saga.step;


import ru.voleshko.grocery.ordersaga.dto.Order;

public interface SagaStep {

    void doStep(Order order);

    void handleError(Order order);

}
