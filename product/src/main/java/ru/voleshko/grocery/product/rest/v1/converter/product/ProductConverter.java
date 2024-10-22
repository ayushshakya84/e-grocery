package ru.voleshko.grocery.product.rest.v1.converter.product;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Product;
import ru.voleshko.grocery.product.domain.repository.ProductRepository;
import ru.voleshko.grocery.product.exception.EntityNotFoundException;
import ru.voleshko.grocery.product.rest.v1.converter.DtoConverter;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductDto;

import java.util.UUID;

@DecoratedWith(value = ProductConverterDecorator.class)
@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public abstract class ProductConverter implements DtoConverter<Product, ProductDto> {

    @Override
    public abstract ProductDto fromDomain(Product entity);

}

abstract class ProductConverterDecorator extends ProductConverter {

    @Autowired
    @Qualifier("delegate")
    private ProductConverter delegate;

    @Autowired
    private ProductRepository repository;

    @Override
    @Transactional
    public ProductDto fromDomain(Product entity) {
        UUID id = entity.getId();
        return delegate.fromDomain(repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product not found")));
    }
}
