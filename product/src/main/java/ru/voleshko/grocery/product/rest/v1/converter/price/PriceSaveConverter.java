package ru.voleshko.grocery.product.rest.v1.converter.price;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Price;
import ru.voleshko.grocery.product.rest.v1.converter.SaveDtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.price.PriceSaveDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriceSaveConverter extends SaveDtoConverter<Price, PriceSaveDto> {

    @Override
    Price toDomain(PriceSaveDto dto);
}
