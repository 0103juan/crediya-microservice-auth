package co.com.pragma.api.response;

import java.time.OffsetDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

/**
* ErrorResponse
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL) // No incluye campos nulos en el JSON de respuesta
public class ErrorResponse {

    private OffsetDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;

    // Este campo es clave para los detalles de validación.
    // Usamos Map<String, Object> para mayor flexibilidad.
    private Map<String, ?> details;

    // Constructor simplificado para errores sin detalles
    public ErrorResponse(OffsetDateTime timestamp, int status, String error, String message, String path) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }
}