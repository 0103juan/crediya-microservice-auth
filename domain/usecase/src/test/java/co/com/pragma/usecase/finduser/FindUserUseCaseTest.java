package co.com.pragma.usecase.finduser;

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

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FindUserUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private FindUserUseCase findUserUseCase;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .idNumber("123456789")
                .email("john.doe@example.com")
                .build();
    }

    @Test
    @DisplayName("Debería encontrar un usuario por su número de documento")
    void findByIdNumber_shouldReturnUser() {
        
        when(userRepository.findByIdNumber("123456789")).thenReturn(Mono.just(testUser));

        
        Mono<User> result = findUserUseCase.findByIdNumber("123456789");

        
        StepVerifier.create(result)
                .expectNext(testUser)
                .verifyComplete();
    }

    @Test
    @DisplayName("Debería devolver vacío si no encuentra usuario por email")
    void findByEmail_whenUserNotFound_shouldReturnEmpty() {
        
        when(userRepository.findByEmail("john.doe@example.com")).thenReturn(Mono.empty());

        
        Mono<User> result = findUserUseCase.findByEmail("john.doe@example.com");

        
        StepVerifier.create(result)
                .verifyComplete();
    }
}