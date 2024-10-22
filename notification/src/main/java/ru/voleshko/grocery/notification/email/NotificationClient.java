package ru.voleshko.grocery.notification.email;

public interface NotificationClient {

    void send(String title, String body, String recipientAddress);
}
