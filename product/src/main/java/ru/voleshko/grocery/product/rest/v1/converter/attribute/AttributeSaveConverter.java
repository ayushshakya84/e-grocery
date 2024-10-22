package ru.voleshko.grocery.product.rest.v1.converter.attribute;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Attribute;
import ru.voleshko.grocery.product.rest.v1.converter.SaveDtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.attribute.AttributeSaveDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttributeSaveConverter extends SaveDtoConverter<Attribute, AttributeSaveDto> {

    @Override
    Attribute toDomain(AttributeSaveDto dto);
}
