package co.com.pragma.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* Datos del usuario creado, excluyendo información sensible.
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private String id = null;
    private String nombres = null;
    private String apellidos = null;
    private String email = null;
}