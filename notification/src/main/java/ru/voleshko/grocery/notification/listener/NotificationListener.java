package ru.voleshko.grocery.notification.listener;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.notification.NotificationService;
import ru.voleshko.grocery.notification.dto.NotificationDto;
import ru.voleshko.lib.idempotency.Idempotent;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @SneakyThrows
    @Transactional
    @Idempotent(operation = "NOTIFICATION")
    @RabbitListener(queues = "orderNotificationQueue")
    public void receiveNotification(String notificationDto, @Header("Idempotency-key") UUID idempotencyKey) {
        NotificationDto dto = objectMapper.readValue(notificationDto, NotificationDto.class);
        notificationService.sendNotification(dto);
    }
}
