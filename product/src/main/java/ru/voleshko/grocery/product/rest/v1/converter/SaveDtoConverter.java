package ru.voleshko.grocery.product.rest.v1.converter;

public interface SaveDtoConverter<ENTITY, DTO> {

    ENTITY toDomain(DTO dto);

}
