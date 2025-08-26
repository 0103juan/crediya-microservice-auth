package co.com.pragma.api.response;

import lombok.*;

/**
* Datos del user creado, excluyendo información sensible.
*/

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private String firstName = null;
    private String lastName = null;
    private String email = null;
    private Long idNumber = null;
    private String description = null;
}