package ru.voleshko.grocery.product.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ProductCatalogueException extends RuntimeException {

	private static final long serialVersionUID = -1888559756029909059L;

	private final HttpStatus httpStatus;

	public ProductCatalogueException(HttpStatus status, String message) {
		super(message);
		this.httpStatus = status;
	}
}
