package co.com.pragma.model.role;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Getter
@RequiredArgsConstructor
public enum Role {
    ROLE_ADMIN(1, "Administrador del sistema con todos los permisos"),
    ROLE_CLIENTE(2, "Cliente que puede solicitar productos y ver su estado"),
    ROLE_ASESOR(3, "Asesor de crédito con permisos para gestionar solicitudes");


    private final Integer id;
    private final String description;

    public static Role of(int id) {
        return Stream.of(Role.values())
                .filter(r -> r.getId() == id)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("ID de rol inválido: " + id));
    }
}