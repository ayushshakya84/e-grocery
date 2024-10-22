package ru.voleshko.grocery.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ConflictException extends ProductCatalogueException {

	private static final long serialVersionUID = -153554927448962704L;

    public ConflictException(String message) {
        super(HttpStatus.CONFLICT, message);
    }
}
