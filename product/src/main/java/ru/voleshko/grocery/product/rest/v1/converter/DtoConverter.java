package ru.voleshko.grocery.product.rest.v1.converter;

public interface DtoConverter<ENTITY, DTO> {

    DTO fromDomain(ENTITY entity);

}
