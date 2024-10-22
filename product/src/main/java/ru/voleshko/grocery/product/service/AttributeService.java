package ru.voleshko.grocery.product.service;

import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Attribute;
import ru.voleshko.grocery.product.domain.repository.AttributeRepository;
import ru.voleshko.grocery.product.exception.EntityNotFoundException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AttributeService {

    private final AttributeRepository repository;

    @Transactional(readOnly = true)
    public Page<Attribute> findAll(Predicate predicate, Pageable pageable) {
        return repository.findAll(predicate, pageable);
    }

    @Transactional(readOnly = true)
    public Attribute findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attribute not found"));
    }

    @Transactional
    public Attribute save(Attribute attribute) {
        return repository.save(attribute);
    }

    @Transactional
    public Attribute update(Attribute attribute, UUID id) {
        Attribute fromDb = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Attribute not found"));
        BeanUtils.copyProperties(attribute, fromDb, "id");
        return fromDb;
    }
}
