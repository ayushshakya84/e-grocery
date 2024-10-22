package ru.voleshko.grocery.product.rest.v1.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.product.rest.v1.converter.price.PriceConverter;
import ru.voleshko.grocery.product.rest.v1.dto.price.PriceDto;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductDto;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductSaveDto;
import ru.voleshko.grocery.product.service.ProductService;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;
import static ru.voleshko.grocery.product.rest.common.HttpStatusUtil.created;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductApiController implements ProductApi {

    private final ProductService productService;
    private final PriceConverter priceConverter;

    @Override
    public ResponseEntity<ProductDto> create(@RequestBody ProductSaveDto product) {
        return created(productService.save(product));
    }

    @Override
    public ResponseEntity<ProductDto> update(
            @RequestBody ProductSaveDto product,
            @PathVariable("id") UUID id
    ) {
        return ok(productService.update(product, id));
    }

    @Override
    public void delete(@PathVariable("id") UUID id) {
        productService.deleteById(id);
    }

    @Override
    public ResponseEntity<List<PriceDto>> getPricesForTimeRange(@PathVariable("id") UUID id) {
        return ok(productService.getPricesForTimeRange(id).stream()
                .map(priceConverter::fromDomain)
                .collect(toList()));
    }

}
