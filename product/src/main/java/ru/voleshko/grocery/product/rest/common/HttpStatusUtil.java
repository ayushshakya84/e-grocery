package ru.voleshko.grocery.product.rest.common;

import lombok.experimental.UtilityClass;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@UtilityClass
public class HttpStatusUtil {

    public static <T> ResponseEntity<T> created(T obj) {
        return ResponseEntity.status(HttpStatus.CREATED).body(obj);
    }

}
