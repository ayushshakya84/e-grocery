package ru.voleshko.grocery.product.rest.v1.converter.price;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Price;
import ru.voleshko.grocery.product.rest.v1.converter.DtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.price.PriceDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PriceConverter extends DtoConverter<Price, PriceDto> {

    @Override
    PriceDto fromDomain(Price entity);
}
