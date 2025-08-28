package co.com.pragma.api.mapper;

import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.UserResponse;
import co.com.pragma.model.exceptions.InvalidRoleException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);
    private User user;
    private RegisterUserRequest request;

    @BeforeEach
    void setUp() {
        request = new RegisterUserRequest();
        request.setFirstName("Juan");
        request.setLastName("Perez");
        request.setEmail("juan.perez@example.com");
        request.setIdNumber("12345678");
        request.setPhone(3101234567L);
        request.setBaseSalary(2500000.0);
        request.setBirthDate(LocalDate.of(1995, 5, 10));
        request.setAddress("Calle Falsa 123");
        request.setRole("ROLE_CLIENTE");

        user = User.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("juan.perez@example.com")
                .idNumber("12345678")
                .phone(3101234567L)
                .baseSalary(2500000.0)
                .birthDate(LocalDate.of(1995, 5, 10))
                .address("Calle Falsa 123")
                .role(Role.ROLE_CLIENTE)
                .build();
    }


    @Test
    void shouldMapRegisterUserRequestToUser() {
        // Act
        User mappedUser = userMapper.toModel(request);

        // Assert
        assertNotNull(mappedUser);
        assertEquals("Juan", mappedUser.getFirstName());
        assertEquals("Perez", mappedUser.getLastName());
        assertEquals("juan.perez@example.com", mappedUser.getEmail());
        assertEquals("12345678", mappedUser.getIdNumber());
        assertEquals(3101234567L, mappedUser.getPhone());
        assertEquals(2500000.0, mappedUser.getBaseSalary());
        assertEquals(LocalDate.of(1995, 5, 10), mappedUser.getBirthDate());
        assertEquals("Calle Falsa 123", mappedUser.getAddress());
        assertEquals(Role.ROLE_CLIENTE, mappedUser.getRole());
    }

    @Test
    void shouldMapUserToUserResponse() {
        // Act
        UserResponse response = userMapper.toResponse(user);

        // Assert
        assertNotNull(response);
        assertEquals(user.getFirstName(), response.getFirstName());
        assertEquals(user.getLastName(), response.getLastName());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(user.getIdNumber(), response.getIdNumber());
    }

    @Test
    void shouldMapUserToUserDTO() {
        // Act
        UserDTO dto = userMapper.toDTO(user);

        // Assert
        assertNotNull(dto);
        assertEquals(user.getFirstName(), dto.firstName());
        assertEquals(user.getEmail(), dto.email());
        assertEquals(user.getRole(), dto.role());
    }

    @Test
    void shouldMapUserListToUserDTOList() {
        // Arrange
        List<User> userList = List.of(user);

        // Act
        List<UserDTO> dtoList = userMapper.toListDTO(userList);

        // Assert
        assertNotNull(dtoList);
        assertEquals(1, dtoList.size());
        assertEquals(user.getFirstName(), dtoList.get(0).firstName());
    }

    @Test
    void shouldThrowInvalidRoleExceptionForInvalidRole() {
        // Act & Assert
        InvalidRoleException exception = assertThrows(InvalidRoleException.class, () -> {
            userMapper.toRole("ROLE_INVALIDO");
        });

        assertEquals("El rol 'ROLE_INVALIDO' no es válido.", exception.getMessage());
    }
}