package co.com.pragma.api.config;

import co.com.pragma.api.response.ErrorResponse;
import co.com.pragma.model.exceptions.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Collections;
import static org.junit.jupiter.api.Assertions.assertEquals;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleEmailAlreadyExists() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("El email ya existe.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleEmailAlreadyExists(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.CONFLICT, entity.getStatusCode());
                    Assertions.assertNotNull(entity.getBody());
                    assertEquals("El email ya existe.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleIdNumberAlreadyExists() {
        IdNumberAlreadyExistsException ex = new IdNumberAlreadyExistsException("El ID ya existe.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleIdNumberAlreadyExists(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.CONFLICT, entity.getStatusCode());
                    Assertions.assertNotNull(entity.getBody());
                    assertEquals("El ID ya existe.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException("Usuario no encontrado.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleUserNotFound(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.NOT_FOUND, entity.getStatusCode());
                    Assertions.assertNotNull(entity.getBody());
                    assertEquals("Usuario no encontrado.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleInvalidRole() {
        InvalidRoleException ex = new InvalidRoleException("Rol inválido.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleInvalidRole(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                    Assertions.assertNotNull(entity.getBody());
                    assertEquals("Rol inválido.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }

    @Test
    void handleUserValidationException() {
        UserValidationException ex = new UserValidationException("Error de validación.", Collections.singletonMap("campo", "mensaje"));
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleValidationException(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.BAD_REQUEST, entity.getStatusCode());
                    Assertions.assertNotNull(entity.getBody());
                    assertEquals("Error de validación.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }
}