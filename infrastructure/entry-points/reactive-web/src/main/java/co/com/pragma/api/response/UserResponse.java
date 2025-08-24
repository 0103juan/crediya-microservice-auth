package co.com.pragma.api.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* Datos del user creado, excluyendo información sensible.
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String nombres = null;
    private String apellidos = null;
    private String email = null;
    private String description = null;
}