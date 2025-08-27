package co.com.pragma.api.config;

import co.com.pragma.api.response.ErrorResponse;
import co.com.pragma.model.exceptions.*;

import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(Exception ex, ServerWebExchange exchange) {
        log.error("Error interno del servidor en la ruta: {}", exchange.getRequest().getPath(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                "Ocurrió un error inesperado.",
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleEmailAlreadyExists(EmailAlreadyExistsException ex, ServerWebExchange exchange) {
        log.error("Conflicto: El email ya existe. Path: {}", exchange.getRequest().getPath(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(IdNumberAlreadyExistsException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIdNumberAlreadyExists(IdNumberAlreadyExistsException ex, ServerWebExchange exchange) {
        log.error("Conflicto: El ID Number ya existe. Path: {}", exchange.getRequest().getPath(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUserNotFound(UserNotFoundException ex, ServerWebExchange exchange) {
        log.error("No encontrado: El recurso solicitado no existe. Path: {}", exchange.getRequest().getPath(), ex);

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND));
    }

    @ExceptionHandler(InvalidRoleException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidRole(InvalidRoleException ex, ServerWebExchange exchange) {
        log.warn("Se recibió un rol inválido en la petición: {}", ex.getMessage());

        ErrorResponse errorResponse = new ErrorResponse(
                OffsetDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                ex.getMessage(),
                exchange.getRequest().getPath().toString()
        );
        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }

    @ExceptionHandler(UserValidationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(UserValidationException ex, ServerWebExchange exchange) {
        log.warn("Errores de validación detectados en la ruta: {}", exchange.getRequest().getPath());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(OffsetDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(exchange.getRequest().getPath().toString())
                .details(ex.getErrors())
                .build();

        return Mono.just(new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST));
    }
}