package ru.voleshko.grocery.product.rest.v1.converter.reservation;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Reservation;
import ru.voleshko.grocery.product.rest.v1.converter.SaveDtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.reservation.ReservationSaveDto;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { ReservationItemSaveConverter.class })
public interface ReservationSaveConverter extends SaveDtoConverter<Reservation, ReservationSaveDto> {

    @Override
    Reservation toDomain(ReservationSaveDto dto);
}
