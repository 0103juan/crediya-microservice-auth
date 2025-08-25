package co.com.pragma.model.exceptions;

import lombok.Getter;

import java.util.Map;

@Getter
public class UserValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public UserValidationException(String message, Map<String, String> errors) {
        super(message);
        this.errors = errors;
    }
}