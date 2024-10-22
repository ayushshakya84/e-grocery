package ru.voleshko.grocery.product.rest.v1.controller;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.voleshko.grocery.product.domain.model.Category;
import ru.voleshko.grocery.product.rest.common.PageDto;
import ru.voleshko.grocery.product.rest.common.PageInfoDto;
import ru.voleshko.grocery.product.rest.v1.converter.category.CategoryConverter;
import ru.voleshko.grocery.product.rest.v1.converter.category.CategorySaveConverter;
import ru.voleshko.grocery.product.rest.v1.dto.category.CategoryDto;
import ru.voleshko.grocery.product.rest.v1.dto.category.CategorySaveDto;
import ru.voleshko.grocery.product.service.CategoryService;

import java.util.List;
import java.util.UUID;

import static java.util.stream.Collectors.toList;
import static org.springframework.http.ResponseEntity.ok;
import static ru.voleshko.grocery.product.rest.common.HttpStatusUtil.created;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryApiController implements CategoryApi {

    private final CategoryService categoryService;
    private final CategoryConverter categoryConverter;
    private final CategorySaveConverter categorySaveConverter;

    @Override
    public ResponseEntity<CategoryDto> create(@RequestBody CategorySaveDto category) {
        Category saved = categoryService.save(categorySaveConverter.toDomain(category));
        return created(categoryConverter.fromDomain(saved));
    }

    @Override
    public ResponseEntity<PageDto<CategoryDto>> findAll(Predicate predicate, Pageable pageable) {
        if (predicate == null) {
            predicate = new BooleanBuilder();
        }
        Page<Category> page = categoryService.findAll(predicate, pageable);
        List<CategoryDto> attributes = page.getContent().stream()
                .map(categoryConverter::fromDomain)
                .collect(toList());

        return ok(new PageDto<>(attributes, PageInfoDto.pageInfoDtoOf(page)));
    }

    public ResponseEntity<CategoryDto> findById(@PathVariable("id") UUID id) {
        return ok(categoryConverter.fromDomain(categoryService.findById(id)));
    }

    public ResponseEntity<CategoryDto> update(
            @RequestBody CategorySaveDto category,
            @PathVariable("id") UUID id
    ) {
        Category updated = categoryService.update(categorySaveConverter.toDomain(category), id);
        return ok(categoryConverter.fromDomain(updated));
    }

    public void deleteById(@PathVariable("id") UUID id) {
        categoryService.deleteById(id);
    }

    public ResponseEntity<List<CategoryDto>> getSubCategories(
            @RequestParam(value = "parentId", required = false) UUID parentId
    ) {
        return ok(categoryService.getSubCategories(parentId)
                .stream()
                .map(categoryConverter::fromDomain)
                .collect(toList()));
    }

    public ResponseEntity<List<CategoryDto>> getRootCategories() {
        return ok(categoryService.getRootCategories()
                .stream()
                .map(categoryConverter::fromDomain)
                .collect(toList()));
    }

    public ResponseEntity<List<CategoryDto>> getLeafCategories() {
        return ok(categoryService.getLeafCategories()
                .stream()
                .map(categoryConverter::fromDomain)
                .collect(toList()));
    }

    public void addAttributeToCategory(
            @NonNull @RequestParam(value = "categoryId", required = true) UUID categoryId,
            @NonNull @RequestParam(value = "attributeId", required = true) UUID attributeId
    ) {
        categoryService.addAttributeToCategory(categoryId, attributeId);
    }

    public void deleteAttributeFromCategory (
            @NonNull @RequestParam(value = "categoryId", required = true) UUID categoryId,
            @NonNull @RequestParam(value = "attributeId", required = true) UUID attributeId
    ) {
        categoryService.deleteAttributeFromCategory(categoryId, attributeId);
    }
}
