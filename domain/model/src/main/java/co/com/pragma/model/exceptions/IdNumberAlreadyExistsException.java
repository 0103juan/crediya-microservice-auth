package co.com.pragma.model.exceptions;

public class IdNumberAlreadyExistsException extends RuntimeException {
    public IdNumberAlreadyExistsException(String message) {
        super(message);
    }
}
