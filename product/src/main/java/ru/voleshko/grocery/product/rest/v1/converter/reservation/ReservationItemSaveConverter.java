package ru.voleshko.grocery.product.rest.v1.converter.reservation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Product;
import ru.voleshko.grocery.product.domain.model.ReservationItem;
import ru.voleshko.grocery.product.rest.v1.converter.SaveDtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.reservation.ReservationSaveDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        imports = {Product.class}
)
public interface ReservationItemSaveConverter extends SaveDtoConverter<ReservationItem, ReservationSaveDto.ReservationItemSaveDto> {

    @Override
    @Mapping(target = "product", expression = "java( new Product(dto.getProductId(), null, null, null, null, null, null, 0, null) )")
    @Mapping(target = "reservation", ignore = true)
    ReservationItem toDomain(ReservationSaveDto.ReservationItemSaveDto dto);
}
