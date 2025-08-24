package co.com.pragma.model.user;
import co.com.pragma.model.rol.Rol;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String nombre;
    private String apellido;
    private String email;
    private Integer documento_identidad;
    private Long telefono;
    private Double salario_base;
    private Rol rol;
}
