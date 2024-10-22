package ru.voleshko.grocery.product.rest.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.voleshko.grocery.product.exception.ProductCatalogueException;

@ControllerAdvice
public class CommonExceptionHandler {

    @ExceptionHandler(ProductCatalogueException.class)
    public ResponseEntity handleException(ProductCatalogueException e) {
        return ResponseEntity.status(e.getHttpStatus()).body(e.getMessage());
    }
}
