package ru.voleshko.grocery.profile.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public abstract class ProfileException extends RuntimeException {

	private static final long serialVersionUID = -1888559756029909059L;

	private final HttpStatus httpStatus;

	public ProfileException(HttpStatus status, String message) {
		super(message);
		this.httpStatus = status;
	}
}
