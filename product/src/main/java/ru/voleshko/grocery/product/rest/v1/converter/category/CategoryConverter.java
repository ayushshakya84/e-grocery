package ru.voleshko.grocery.product.rest.v1.converter.category;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Category;
import ru.voleshko.grocery.product.domain.repository.CategoryRepository;
import ru.voleshko.grocery.product.exception.EntityNotFoundException;
import ru.voleshko.grocery.product.rest.v1.converter.DtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.category.CategoryDto;

import java.util.UUID;

@DecoratedWith(value = CategoryConverterDecorator.class)
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class CategoryConverter implements DtoConverter<Category, CategoryDto> {

    @Override
    public abstract CategoryDto fromDomain(Category entity);

}

abstract class CategoryConverterDecorator extends CategoryConverter {

    @Autowired
    @Qualifier("delegate")
    private CategoryConverter delegate;

    @Autowired
    private CategoryRepository repository;

    @Override
    @Transactional
    public CategoryDto fromDomain(Category entity) {
        UUID id = entity.getId();
        return delegate.fromDomain(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found")));
    }
}
