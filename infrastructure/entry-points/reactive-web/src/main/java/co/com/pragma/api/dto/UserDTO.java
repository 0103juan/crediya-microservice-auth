package co.com.pragma.api.dto;

import co.com.pragma.model.rol.Rol;

public record UserDTO(String nombre,
                         String apellido,
                         String email,
                         Integer documento_identidad,
                         Long telefono,
                         Double salario_base,
                         Rol rol) {
}
