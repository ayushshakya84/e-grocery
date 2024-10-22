package ru.voleshko.grocery.profile.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EntityNotFoundException extends ProfileException {

	private static final long serialVersionUID = -153554927448962704L;

    public EntityNotFoundException(String message) {
        super(HttpStatus.NOT_FOUND, message);
    }
}
