package ru.voleshko.grocery.ordersaga.config;

import org.springframework.amqp.core.Declarables;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static ru.voleshko.grocery.ordersaga.config.DirectQueueBinder.createDirectQueues;

@Configuration
public class MqConfig {

    @Bean
    public Declarables paymentBindings() {
        return createDirectQueues(
                "paymentExchange",
                "paymentRequest", "paymentSuccess", "paymentFail", "paymentRollback");
    }

    @Bean
    public Declarables reservationBindings() {
        return createDirectQueues(
                "reservationExchange",
                "reservationRequest", "reservationSuccess", "reservationFail", "reservationRollback");
    }

    @Bean
    public Declarables shipmentBindings() {
        return createDirectQueues(
                "shipmentExchange",
                "shipmentRequest", "shipmentSuccess", "shipmentFail", "shipmentRollback");
    }

    @Bean
    public Declarables orderBindings() {
        return createDirectQueues(
                "orderExchange",
                "orderStatusRequest", "orderStatusSuccess", "orderStatusFail", "changeOrderStatus");
    }

    @Bean
    public Declarables productBindings() {
        return createDirectQueues(
                "productExchange",
                "productUpserted", "productDeleted");
    }

    @Bean
    public Declarables notificationBindings() {
        return createDirectQueues(
                "notificationExchange", "orderNotification"
        );
    }

    @Bean
    public Declarables sagaBindings() {
        return createDirectQueues(
                "sagaExchange", "startSaga"
        );
    }
}

