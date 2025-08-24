package co.com.pragma.model.role;
import lombok.*;


import java.util.stream.Stream;

public enum Role {
    ROLE_ADMIN(1, "Administrador del sistema con todos los permisos"),
    ROLE_CLIENTE(2, "Cliente que puede solicitar productos y ver su estado"),
    ROLE_ASESOR(3, "Asesor de crédito con permisos para gestionar solicitudes");


    private final Integer id;
    private final String description; // <-- Nuevo campo

    // Constructor actualizado
    Role(Integer id, String description) {
        this.id = id;
        this.description = description;
    }

    public Integer getId() {
        return this.id;
    }

    // Getter para la descripción
    public String getDescription() {
        return this.description;
    }

    public static Role of(int id) {
        return Stream.of(Role.values())
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ID de rol inválido: " + id));
    }
}