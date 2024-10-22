package ru.voleshko.grocery.product.rest.v1.converter.attribute;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Attribute;
import ru.voleshko.grocery.product.rest.v1.converter.DtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.attribute.AttributeDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AttributeConverter extends DtoConverter<Attribute, AttributeDto> {

    @Override
    AttributeDto fromDomain(Attribute entity);
}
