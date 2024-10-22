package ru.voleshko.grocery.product.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.voleshko.grocery.product.domain.model.Category;

import java.util.List;
import java.util.UUID;

public interface CategoryRepository extends
        JpaRepository<Category, UUID>,
        DefaultQueryDslRepository<Category> {

    @Query(value = "select count(*) > 0 from category_product where category_id=:categoryId", nativeQuery = true)
    boolean hasAssociatedProducts(UUID categoryId);

    @Query(value = "select count(*) > 0 from category where parent_category_id=:categoryId", nativeQuery = true)
    boolean isParentCategory(UUID categoryId);

    List<Category> findByParentCategory(Category parentCategory);

    @Modifying
    @Query(value = "WITH RECURSIVE tree_down AS (" +
            " SELECT c.* " +
            " FROM category c " +
            " WHERE c.id = :categoryId " +
            "" +
            " UNION " +
            "" +
            "SELECT c.* " +
            " FROM category c " +
            " JOIN tree_down td ON td.id = c.parent_category_id " +
            ") " +
            "DELETE FROM category WHERE id IN (SELECT id FROM tree_down );"
        , nativeQuery = true
    )
    void removeSubTree(UUID categoryId);
}
