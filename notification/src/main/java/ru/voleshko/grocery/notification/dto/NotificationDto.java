package ru.voleshko.grocery.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationDto {

    private String type;
    private String recipientEmail;
    private Map<String, String> notification;

}
