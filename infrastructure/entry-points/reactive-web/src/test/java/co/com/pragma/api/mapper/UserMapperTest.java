package co.com.pragma.api.mapper;

import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    // Obtenemos una instancia del mapper generado por MapStruct
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapRegisterUserRequestToUser() {
        // Arrange: Creamos un objeto de entrada (la petición de la API)
        RegisterUserRequest request = new RegisterUserRequest();
        request.setFirstName("Juan");
        request.setLastName("Perez");
        request.setEmail("juan.perez@example.com");
        request.setIdNumber(12345678L);
        request.setPhone(3101234567L);
        request.setBaseSalary(2500000.0);
        request.setBirthDate(LocalDate.of(1995, 5, 10));
        request.setAddress("Calle Falsa 123");
        request.setRole("ROLE_CLIENTE");

        // Act: Ejecutamos el método de mapeo
        User user = userMapper.toModel(request);

        // Assert: Verificamos que todos los campos se mapearon correctamente
        assertNotNull(user);
        assertEquals("Juan", user.getFirstName());
        assertEquals("Perez", user.getLastName());
        assertEquals("juan.perez@example.com", user.getEmail());
        // El idNumber en User es Integer, hacemos casting
        assertEquals(12345678, user.getIdNumber().intValue());
        assertEquals(3101234567L, user.getPhone());
        assertEquals(2500000.0, user.getBaseSalary());
        assertEquals(LocalDate.of(1995, 5, 10), user.getBirthDate());
        assertEquals("Calle Falsa 123", user.getAddress());
        // El rol se debe mapear al enum correspondiente
        assertNotNull(user.getRole());
    }
}