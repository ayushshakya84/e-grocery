package ru.voleshko.grocery.product.service;

import com.google.common.collect.Lists;
import com.querydsl.core.types.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.voleshko.grocery.product.domain.model.Attribute;
import ru.voleshko.grocery.product.domain.model.Category;
import ru.voleshko.grocery.product.domain.model.QCategory;
import ru.voleshko.grocery.product.domain.repository.CategoryRepository;
import ru.voleshko.grocery.product.exception.BadRequestException;
import ru.voleshko.grocery.product.exception.EntityNotFoundException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository repository;
    private final AttributeService attributeService;

    @Transactional(readOnly = true)
    public Page<Category> findAll(Predicate predicate, Pageable pageable) {
        return repository.findAll(predicate, pageable);
    }

    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Transactional
    public Category save(Category category) {
        // parent category is not a leaf now
        Category parent = category.getParentCategory();
        parent.setLeaf(false);
        repository.save(parent);

        return repository.save(category);
    }

    @Transactional
    public Category update(Category category, UUID id) {
        Category fromDb = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Category not found"));
        BeanUtils.copyProperties(category, fromDb, "id", "parentCategory", "attributes");
        return fromDb;
    }

    @Transactional
    public void deleteById(UUID id) {
        if (repository.hasAssociatedProducts(id)) {
            throw new BadRequestException("This Category has associated products. Can not delete!");
        }
        // now parent category becomes a leaf
        Category category = findById(id);
        Category parent = category.getParentCategory();
        parent.setLeaf(true);
        repository.save(parent);

        // remove sub-tree
        repository.removeSubTree(id);
    }

    @Transactional
    public void deleteAttributeFromCategory(UUID categoryId, UUID attributeId) {
        // category must be a leaf (must not be a parent to any other category)
        if (repository.isParentCategory(categoryId)) {
            throw new BadRequestException("Category is not a leaf of hierarchy tree");
        }

        Category category = findById(categoryId);
        Attribute attribute = attributeService.findById(attributeId);

        List<Attribute> attributes = category.getAttributes();
        if (!attributes.contains(attribute)) {
            throw new BadRequestException("Category does not contain such attribute");
        }
        attributes.remove(attribute);
    }

    @Transactional
    public void addAttributeToCategory(UUID categoryId, UUID attributeId) {
        // category must be a leaf (must not be a parent to any other category)
        if (repository.isParentCategory(categoryId)) {
            throw new BadRequestException("Category is not a leaf of hierarchy tree");
        }

        Category category = findById(categoryId);
        Attribute attribute = attributeService.findById(attributeId);

        List<Attribute> attributes = category.getAttributes();
        if (attributes.contains(attribute)) {
            throw new BadRequestException("Category already has such attribute");
        }
        attributes.add(attribute);
    }

    @Transactional(readOnly = true)
    public List<Category> getRootCategories() {
        return Lists.newArrayList(repository.findAll(QCategory.category.parentCategory.isNull()));
    }

    @Transactional(readOnly = true)
    public List<Category> getLeafCategories() {
        return Lists.newArrayList(repository.findAll(QCategory.category.isLeaf.isTrue()));
    }

    @Transactional(readOnly = true)
    public List<Category> getSubCategories(UUID parentId) {
        return repository.findByParentCategory(findById(parentId));
    }
}
