package co.com.pragma.requestvalidator;

import co.com.pragma.model.exceptions.UserValidationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

class RequestValidatorTest {

    private RequestValidator requestValidator;

    @BeforeEach
    void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        requestValidator = new RequestValidator(validator);
    }

    @Test
    void validate_whenObjectIsValid_shouldReturnMonoWithObject() {
        // Arrange: Un objeto que cumple todas las reglas de validación.
        ValidTestObject validObject = new ValidTestObject("test");

        // Act & Assert
        StepVerifier.create(requestValidator.validate(validObject))
                .expectNext(validObject)
                .verifyComplete();
    }

    @Test
    void validate_whenObjectIsInvalid_shouldReturnMonoError() {
        // Arrange: Un objeto que NO cumple las reglas (el campo es nulo).
        ValidTestObject invalidObject = new ValidTestObject(null);

        // Act & Assert
        StepVerifier.create(requestValidator.validate(invalidObject))
                .expectError(UserValidationException.class)
                .verify();
    }
}

// Clase auxiliar para la prueba con una regla de validación simple.
class ValidTestObject {

    public ValidTestObject(String field) {
    }
}