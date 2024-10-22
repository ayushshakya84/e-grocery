package ru.voleshko.grocery.product.domain.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@EqualsAndHashCode(of = {"id"})
@Table(name = "reservation")
public class Reservation {

    @Id
    @GeneratedValue(generator = "uuid")
    private UUID id;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status = ReservationStatus.CREATED;

    private UUID orderId;

    private ZonedDateTime createdAt = ZonedDateTime.now();

    @OneToMany(mappedBy = "reservation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ReservationItem> reservationItems;

    public Reservation() {
    }

    public Reservation(UUID orderId) {
        this.orderId = orderId;
        this.status = ReservationStatus.CREATED;
        this.createdAt = ZonedDateTime.now();
        this.reservationItems = new ArrayList<>();
    }

    public enum ReservationStatus {
        CREATED, CANCELED, WITHDRAWN
    }

}
