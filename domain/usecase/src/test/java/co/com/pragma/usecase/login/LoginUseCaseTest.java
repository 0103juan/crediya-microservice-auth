// domain/usecase/src/test/java/co/com/pragma/usecase/login/LoginUseCaseTest.java
package co.com.pragma.usecase.login;

import co.com.pragma.model.exceptions.InvalidCredentialsException;
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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoginUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncryptor passwordEncryptor;

    @InjectMocks
    private LoginUseCase loginUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .email("test@example.com")
                .password("encodedPassword")
                .build();
    }

    @Test
    @DisplayName("Debería autenticar al usuario con credenciales válidas")
    void login_withValidCredentials_shouldReturnUser() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(testUser));
        when(passwordEncryptor.matches("rawPassword", "encodedPassword")).thenReturn(true);

        // Act & Assert
        StepVerifier.create(loginUseCase.login("test@example.com", "rawPassword"))
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería devolver error si el usuario no existe")
    void login_whenUserNotFound_shouldReturnError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.empty());

        // Act & Assert
        StepVerifier.create(loginUseCase.login("test@example.com", "rawPassword"))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }

    @Test
    @DisplayName("Debería devolver error si la contraseña es incorrecta")
    void login_withInvalidPassword_shouldReturnError() {
        // Arrange
        when(userRepository.findByEmail("test@example.com")).thenReturn(Mono.just(testUser));
        when(passwordEncryptor.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // Act & Assert
        StepVerifier.create(loginUseCase.login("test@example.com", "wrongPassword"))
                .expectError(InvalidCredentialsException.class)
                .verify();
    }
}