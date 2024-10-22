package ru.voleshko.grocery.product.rest.v1.converter.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Product;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductSaveDto;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = ObjectMapper.class
)
public interface ProductSaveConverter {

    @Mapping(target = "attributes", expression = "java( objectMapper.writeValueAsString(dto.getAttributes()) )")
    @Mapping(target = "name", expression = "java( dto.getName() )")
    @Mapping(target = "description", expression = "java( dto.getDescription() )")
    Product toDomain(ProductSaveDto dto) throws Exception;
}
