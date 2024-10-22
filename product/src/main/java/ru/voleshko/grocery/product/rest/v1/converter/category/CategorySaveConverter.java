package ru.voleshko.grocery.product.rest.v1.converter.category;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.product.domain.model.Category;
import ru.voleshko.grocery.product.rest.v1.converter.SaveDtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.category.CategorySaveDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CategorySaveConverter extends SaveDtoConverter<Category, CategorySaveDto> {

    @Override
    Category toDomain(CategorySaveDto dto);
}
