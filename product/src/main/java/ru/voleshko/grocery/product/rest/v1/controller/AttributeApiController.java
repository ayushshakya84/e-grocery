package ru.voleshko.grocery.product.rest.v1.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.product.domain.model.Attribute;
import ru.voleshko.grocery.product.rest.common.PageDto;
import ru.voleshko.grocery.product.rest.common.PageInfoDto;
import ru.voleshko.grocery.product.rest.v1.converter.attribute.AttributeConverter;
import ru.voleshko.grocery.product.rest.v1.converter.attribute.AttributeSaveConverter;
import ru.voleshko.grocery.product.rest.v1.dto.attribute.AttributeDto;
import ru.voleshko.grocery.product.rest.v1.dto.attribute.AttributeSaveDto;
import ru.voleshko.grocery.product.service.AttributeService;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;
import static ru.voleshko.grocery.product.rest.common.HttpStatusUtil.created;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AttributeApiController implements AttributeApi {

    private final AttributeConverter attributeConverter;
    private final AttributeSaveConverter attributeSaveConverter;
    private final AttributeService attributeService;

    public ResponseEntity<AttributeDto> create(@RequestBody AttributeSaveDto attribute) {
        Attribute saved = attributeService.save(
                attributeSaveConverter.toDomain(attribute)
        );
        return created(attributeConverter.fromDomain(saved));
    }

    @Override
    public ResponseEntity<PageDto<AttributeDto>> findAll(Predicate predicate, Pageable pageable) {
        if (predicate == null) {
            predicate = new BooleanBuilder();
        }
        Page<Attribute> page = attributeService.findAll(predicate, pageable);
        List<AttributeDto> attributes = page.getContent().stream()
                .map(attributeConverter::fromDomain)
                .collect(toList());

        return ok(new PageDto<>(attributes, PageInfoDto.pageInfoDtoOf(page)));
    }

    public ResponseEntity<AttributeDto> findById(@PathVariable("id") UUID id) {
        return ok(attributeConverter.fromDomain(
                attributeService.findById(id))
        );
    }

    public ResponseEntity<AttributeDto> update(@RequestBody AttributeSaveDto attribute, @PathVariable("id") UUID id) {
        Attribute attr = attributeSaveConverter.toDomain(attribute);
        return ok(attributeConverter.fromDomain(
                attributeService.update(attr, id))
        );
    }


}
