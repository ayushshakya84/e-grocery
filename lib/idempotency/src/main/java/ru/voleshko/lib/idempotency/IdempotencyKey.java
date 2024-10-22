package ru.voleshko.lib.idempotency;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = {"key"})
public class IdempotencyKey {

    @Id
    private UUID key;

    private String operation;

    private ZonedDateTime created;

}
