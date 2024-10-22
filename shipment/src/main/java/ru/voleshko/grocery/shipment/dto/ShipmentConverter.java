package ru.voleshko.grocery.shipment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import ru.voleshko.grocery.shipment.entity.Shipment;


@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        uses = { ShipmentItemConverter.class })
public interface ShipmentConverter {

    Shipment toDomain(ShipmentDto dto);
}
