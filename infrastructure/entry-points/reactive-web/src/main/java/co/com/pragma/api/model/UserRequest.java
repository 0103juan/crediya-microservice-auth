package co.com.pragma.api.model;

import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
* Datos para registrar un nuevo usuario en el sistema.
*/

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String nombres = null;
    private String apellidos = null;
    private String email = null;
    private String password = null;
}