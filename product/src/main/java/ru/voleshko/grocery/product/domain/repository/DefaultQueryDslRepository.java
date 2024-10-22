package ru.voleshko.grocery.product.domain.repository;


import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.dsl.StringPath;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.querydsl.binding.MultiValueBinding;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

@NoRepositoryBean
public interface DefaultQueryDslRepository<T> extends QuerydslPredicateExecutor<T>, QuerydslBinderCustomizer {

    @Override
    default void customize(QuerydslBindings bindings, EntityPath entityPath) {
        bindings.bind(String.class).all((MultiValueBinding<StringPath, String>) (path, values) -> {
            BooleanBuilder predicate = new BooleanBuilder();
            values.forEach(value -> predicate.or(path.containsIgnoreCase(value)));
            return Optional.of(predicate);
        });
    }
}
