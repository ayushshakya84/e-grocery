package ru.voleshko.grocery.ordersaga.config;

import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;

import java.util.ArrayList;
import java.util.List;

public final class DirectQueueBinder {

    private DirectQueueBinder() {}

    public static Declarables createDirectQueues(String exchangeName, String... names) {
        List<Declarable> declarables = new ArrayList<>();
        DirectExchange exchange = new DirectExchange(exchangeName);
        declarables.add(exchange);
        for (String name : names) {
            Queue queue = new Queue(name + "Queue", true);
            Declarable binding = BindingBuilder.bind(queue).to(exchange).withQueueName();
            declarables.addAll(List.of(binding, queue));
        }
        return new Declarables(declarables);
    }
}
