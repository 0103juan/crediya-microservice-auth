package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.EmailAlreadyExistsException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .idNumber(123456789)
                .phone(3001234567L)
                .baseSalary(5000000.0)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Prueba de registro exitoso de un nuevo usuario")
    void saveUser_whenEmailDoesNotExist_shouldSaveUser() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(testUser));

        // Act
        Mono<User> result = registerUserUseCase.saveUser(testUser);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de error al registrar un usuario con un email existente")
    void saveUser_whenEmailAlreadyExists_shouldReturnError() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));
        // Se agrega esta línea para evitar el NPE en la construcción del Mono
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.empty());

        // Act
        Mono<User> result = registerUserUseCase.saveUser(testUser);

        // Assert
        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }
}