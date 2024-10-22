package ru.voleshko.grocery.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringSubstitutor;
import org.springframework.stereotype.Service;
import ru.voleshko.grocery.notification.dto.NotificationDto;
import ru.voleshko.grocery.notification.email.NotificationClient;
import ru.voleshko.grocery.notification.entity.NotificationTemplate;
import ru.voleshko.grocery.notification.entity.NotificationTemplateRepository;

import javax.persistence.EntityNotFoundException;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationTemplateRepository templateRepository;
    private final NotificationClient emailClient;

    public void sendNotification(NotificationDto dto) {

        NotificationTemplate template = templateRepository.findById(dto.getType())
                .orElseThrow(() -> new EntityNotFoundException("No notification found for notification type " + dto.getType()));

        String body = resolvePlaceholders(dto.getNotification(), template.getTemplate());
        emailClient.send(template.getTitle(), body, dto.getRecipientEmail());
    }

    private String resolvePlaceholders(Map<String, String> eventPayload, String template) {
        return new StringSubstitutor(eventPayload).replace(template);
    }
}
