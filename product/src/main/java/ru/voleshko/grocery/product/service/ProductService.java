package ru.voleshko.grocery.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Price;
import ru.voleshko.grocery.product.domain.model.Product;
import ru.voleshko.grocery.product.domain.model.QProduct;
import ru.voleshko.grocery.product.domain.repository.PriceRepository;
import ru.voleshko.grocery.product.domain.repository.ProductRepository;
import ru.voleshko.grocery.product.exception.ConflictException;
import ru.voleshko.grocery.product.exception.EntityNotFoundException;
import ru.voleshko.grocery.product.rest.v1.converter.product.ProductConverter;
import ru.voleshko.grocery.product.rest.v1.converter.product.ProductSaveConverter;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductDto;
import ru.voleshko.grocery.product.rest.v1.dto.product.ProductSaveDto;
import ru.voleshko.lib.outbox.entity.OutboxMessage;
import ru.voleshko.lib.outbox.repository.OutboxRepository;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository repository;
    private final PriceRepository priceRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;
    private final ProductSaveConverter productSaveConverter;
    private final ProductConverter productConverter;

    @Transactional(readOnly = true)
    public List<ProductDto> findAll() {
        return repository.findAll().stream()
                .map(productConverter::fromDomain)
                .collect(Collectors.toList());
    }

    @SneakyThrows
    @Transactional
    public ProductDto save(ProductSaveDto productDto) {
        Product product = productSaveConverter.toDomain(productDto);
        if (repository.exists(QProduct.product.name.equalsIgnoreCase(product.getName()))) {
            throw new ConflictException("Product with name " + product.getName() + " already exists");
        }
        Product saved = repository.save(product);
        saveChangedPrice(saved);
        ProductDto dto = productConverter.fromDomain(saved);
        outboxRepository.save(new OutboxMessage(
                "productExchange", "productUpsertedQueue", objectMapper.writeValueAsString(dto))
        );
        return dto;
    }

    @SneakyThrows
    @Transactional
    public ProductDto update(ProductSaveDto product, UUID id) {
        Product fromDb = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Updated product not found"));

        Product toSave = productSaveConverter.toDomain(product);
        if (anotherProductWithSameNameExists(toSave.getId(), toSave.getName())) {
            throw new ConflictException("Product with name " + product.getName() + " already exists");
        }

        if (!fromDb.getPrice().equals(product.getPrice())) {
            fromDb.setPrice(toSave.getPrice());
            saveChangedPrice(fromDb);
        }

        BeanUtils.copyProperties(toSave, fromDb, "id");
        ProductDto dto = productConverter.fromDomain(fromDb);
        outboxRepository.save(new OutboxMessage(
                "productExchange", "productUpsertedQueue", objectMapper.writeValueAsString(dto))
        );

        return dto;
    }

    @Transactional
    public void deleteById(UUID id) {
        findById(id);
        outboxRepository.save(new OutboxMessage(
                "productExchange", "productDeletedQueue", id.toString())
        );
        repository.deleteById(id);
    }

    @Transactional
    public Product findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("No product with id=" + id + " found"));
    }

    @Transactional
    public Optional<Product> findByIdOptionally(UUID id) {
        return repository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Price> getPricesForTimeRange(UUID productId) {
        return priceRepository.findAllByProduct(productId);
    }

    private void saveChangedPrice(Product product) {
        Price price = new Price(null, ZonedDateTime.now(), product.getPrice(), product);
        priceRepository.save(price);
    }

    private boolean anotherProductWithSameNameExists(UUID productId, String productName) {
        return repository.exists(QProduct.product.id.eq(productId).not()
                .and(QProduct.product.name.eq(productName)));
    }
}
