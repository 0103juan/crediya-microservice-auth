package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.EmailAlreadyExistsException;
import co.com.pragma.model.exceptions.IdNumberAlreadyExistsException;
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
                // Aseguramos que el idNumber es un String para consistencia.
                .idNumber("123456789")
                .phone(3001234567L)
                .baseSalary(5000000.0)
                .birthDate(LocalDate.of(1990, 1, 1))
                .address("123 Main St")
                .build();
    }

    @Test
    @DisplayName("Prueba de registro exitoso de un nuevo usuario")
    void saveUser_whenEmailDoesNotExist_shouldSaveUser() {
        // --- ARRANGE: Preparación ---
        // El caso de uso va a llamar a findByEmail. Le decimos que devuelva "vacío" (usuario no encontrado).
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        // El caso de uso TAMBIÉN va a llamar a findByIdNumber. Le decimos que también devuelva "vacío".
        when(userRepository.findByIdNumber(testUser.getIdNumber())).thenReturn(Mono.empty());
        // Finalmente, simulamos la llamada para guardar el usuario.
        when(userRepository.saveUser(any(User.class))).thenReturn(Mono.just(testUser));

        // --- ACT: Ejecución ---
        Mono<User> result = registerUserUseCase.saveUser(testUser);

        // --- ASSERT: Verificación ---
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals("john.doe@example.com"))
                .verifyComplete();
    }

    @Test
    @DisplayName("Prueba de error al registrar un usuario con un email existente")
    void saveUser_whenEmailAlreadyExists_shouldReturnError() {
        // --- ARRANGE: Preparación ---
        // Simulamos que el email SÍ existe.
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.just(testUser));
        // AUN ASÍ, debemos simular la llamada a findByIdNumber, porque el código la ejecuta.
        // En este caso, simulamos que el ID no existe para que el test se centre solo en el error del email.
        when(userRepository.findByIdNumber(testUser.getIdNumber())).thenReturn(Mono.empty());

        // --- ACT: Ejecución ---
        Mono<User> result = registerUserUseCase.saveUser(testUser);

        // --- ASSERT: Verificación ---
        StepVerifier.create(result)
                .expectError(EmailAlreadyExistsException.class)
                .verify();
    }

    @Test
    @DisplayName("Prueba de error al registrar un usuario con un idNumber existente")
    void saveUser_whenIdNumberAlreadyExists_shouldReturnError() {
        // --- ARRANGE: Preparación ---
        // Simulamos que el email NO existe.
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Mono.empty());
        // Y simulamos que el idNumber SÍ existe.
        when(userRepository.findByIdNumber(testUser.getIdNumber())).thenReturn(Mono.just(testUser));

        // --- ACT: Ejecución ---
        Mono<User> result = registerUserUseCase.saveUser(testUser);

        // --- ASSERT: Verificación ---
        StepVerifier.create(result)
                .expectError(IdNumberAlreadyExistsException.class)
                .verify();
    }
}