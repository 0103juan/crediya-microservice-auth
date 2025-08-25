package co.com.pragma.requestvalidator;

import co.com.pragma.model.exceptions.UserValidationException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebInputException;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RequestValidator {

    private final Validator validator;

    public <T> Mono<T> validate(T obj) {
        if (obj == null) {
            return Mono.error(new ServerWebInputException("El cuerpo de la solicitud no puede ser nulo."));
        }

        Set<ConstraintViolation<T>> violations = validator.validate(obj);

        if (violations.isEmpty()) {
            return Mono.just(obj);
        }

        // Convierte las violaciones a un mapa de errores simple
        Map<String, String> errorMap = violations.stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        // Lanza la excepción del dominio con el mapa de errores
        return Mono.error(new UserValidationException("Error de validación de la solicitud.", errorMap));
    }
}