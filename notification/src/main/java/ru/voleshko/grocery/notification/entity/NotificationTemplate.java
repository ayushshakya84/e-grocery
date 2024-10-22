package ru.voleshko.grocery.notification.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTemplate {

    @Id
    private String notificationType;
    private String title;
    private String template;
}
