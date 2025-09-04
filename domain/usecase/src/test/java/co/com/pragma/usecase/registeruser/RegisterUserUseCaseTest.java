package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.DuplicateDataException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncryptor;
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

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptor passwordEncryptor;

    @InjectMocks
    private RegisterUserUseCase registerUserUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .password("rawPassword123")
                .idNumber("123456789")
                .phone(3001234567L)
                .baseSalary(BigDecimal.valueOf(5000000))
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Prueba de registro exitoso de un nuevo usuario")
    void saveUser_whenUserDoesNotExist_shouldSaveUser() {
        when(userRepository.existsByEmailOrIdNumber(testUser.getEmail(), testUser.getIdNumber())).thenReturn(Mono.just(false));
        when(passwordEncryptor.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(testUser));
        Mono<User> result = registerUserUseCase.save(testUser);
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de error al registrar un usuario con un email o idNumber existente")
    void saveUser_whenUserAlreadyExists_shouldReturnError() {
        when(passwordEncryptor.encode(testUser.getPassword())).thenReturn("encodedPassword");
        when(userRepository.existsByEmailOrIdNumber(testUser.getEmail(), testUser.getIdNumber())).thenReturn(Mono.just(true));
        Mono<User> result = registerUserUseCase.save(testUser);
        StepVerifier.create(result)
                .expectError(DuplicateDataException.class)
                .verify();
    }
}