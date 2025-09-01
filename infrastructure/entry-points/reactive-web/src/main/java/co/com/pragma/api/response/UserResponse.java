package co.com.pragma.api.response;

import lombok.*;

/**
* Datos del user creado, excluyendo información sensible.
*/

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String firstName;
    private String lastName;
    private String email;
    private String idNumber;
}