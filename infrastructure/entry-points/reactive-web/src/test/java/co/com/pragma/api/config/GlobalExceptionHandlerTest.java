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
import static org.junit.jupiter.api.Assertions.assertNotNull;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

    @Test
    void handleGenericException() {
        Exception ex = new Exception("Error inesperado.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleGenericException(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, entity.getStatusCode());
                    assertNotNull(entity.getBody());
                    assertEquals("Ocurrió un error inesperado.", entity.getBody().getMessage());
                    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), entity.getBody().getError());
                })
                .verifyComplete();
    }

    @Test
    void handleDuplicateDataException() {
        DuplicateDataException ex = new DuplicateDataException("El usuario ya existe.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleUserExists(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.CONFLICT, entity.getStatusCode());
                    assertNotNull(entity.getBody());
                    assertEquals("El usuario ya existe.", entity.getBody().getMessage());
                })
                .verifyComplete();
    }


    @Test
    void handleEmailAlreadyExists() {
        EmailAlreadyExistsException ex = new EmailAlreadyExistsException("El email ya existe.");
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/test").build());
        Mono<ResponseEntity<ErrorResponse>> response = exceptionHandler.handleEmailAlreadyExists(ex, exchange);

        StepVerifier.create(response)
                .assertNext(entity -> {
                    assertEquals(HttpStatus.CONFLICT, entity.getStatusCode());
                    assertNotNull(entity.getBody());
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
                    assertNotNull(entity.getBody());
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
                    assertNotNull(entity.getBody());
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
                    assertNotNull(entity.getBody());
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
                    assertNotNull(entity.getBody());
                    assertEquals("Error de validación.", entity.getBody().getMessage());
                    assertNotNull(entity.getBody().getDetails());
                    assertEquals("mensaje", entity.getBody().getDetails().get("campo"));
                })
                .verifyComplete();
    }
}