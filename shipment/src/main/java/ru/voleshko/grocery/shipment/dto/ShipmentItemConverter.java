package ru.voleshko.grocery.shipment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.shipment.entity.ShipmentItem;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE
)
public interface ShipmentItemConverter {

    @Mapping(target = "shipment", ignore = true)
    ShipmentItem toDomain(ShipmentDto.ShipmentItemDto dto);
}
