package ru.voleshko.grocery.ordersaga.entity;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"id"})
public class AuditLogRecord {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    private UUID orderId;

    private String event;

    private ZonedDateTime createdAt;

}
