package ru.voleshko.grocery.notification.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailClientImpl implements NotificationClient {

    private final JavaMailSender emailSender;

    @Value("${spring.mail.username}")
    private String sendFrom;

    @Override
    public void send(String title, String body, String recipientEmail) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(body);
        message.setTo(recipientEmail);
        message.setFrom(sendFrom);

        log.info("Sending e-mail to [{}]...", recipientEmail);
        emailSender.send(message);
    }
}
