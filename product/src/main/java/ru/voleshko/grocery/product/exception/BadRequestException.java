package ru.voleshko.grocery.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends ProductCatalogueException {

	private static final long serialVersionUID = -153554927448962704L;

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
