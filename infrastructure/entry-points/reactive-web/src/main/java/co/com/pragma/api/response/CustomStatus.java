package co.com.pragma.api.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CustomStatus {

    USER_CREATED_SUCCESSFULLY(HttpStatus.CREATED, "USER_CREATED_SUCCESSFULLY", "Usuario creado exitosamente."),
    USER_FOUND_SUCCESSFULLY(HttpStatus.OK, "USER_FOUND_SUCCESSFULLY", "Usuario encontrado exitosamente."),

    USER_VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "USER_VALIDATION_ERROR", "La solicitud tiene errores de validación."),
    INVALID_ROLE(HttpStatus.BAD_REQUEST, "INVALID_ROLE", "El rol especificado no es válido."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_NOT_FOUND", "El usuario solicitado no existe."),
    USER_ALREADY_EXISTS(HttpStatus.CONFLICT, "USER_ALREADY_EXISTS", "El correo electrónico o el número de documento ya están registrados."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "Ocurrió un error inesperado en el servidor.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}