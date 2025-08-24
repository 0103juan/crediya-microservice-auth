package co.com.pragma.api.response;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* ErrorResponse
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private OffsetDateTime timestamp = null;
    private Integer status = null;
    private String error = null;
    private String message = null;
    private String path = null;
}