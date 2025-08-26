package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.r2dbc.entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserReactiveRepositoryAdapterTest {

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private UserReactiveRepository userReactiveRepository;

    @InjectMocks
    private UserReactiveRepositoryAdapter adapter;

    private User userDomain;
    private UserEntity userEntity;

    @BeforeEach
    void setUp() {
        // Arrange: Preparamos nuestros objetos de prueba
        userDomain = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .build();

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test");

        // --- ESTA ES LA CORRECCIÓN CLAVE Y DEFINITIVA ---
        // Usamos doReturn().when() para un stubbing más seguro y explícito.

        // 1. "Cuando el objectMapper reciba el objeto userDomain para mapearlo a UserEntity.class, DEVUELVE el objeto userEntity"
        // Esto soluciona el error "Strict stubbing" en el test de guardado.
        doReturn(userEntity).when(objectMapper).map(userDomain, UserEntity.class);

        // 2. "Cuando el objectMapper reciba el objeto userEntity para mapearlo a User.class, DEVUELVE el objeto userDomain"
        // Esto soluciona el NullPointerException en el test de búsqueda.
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
    }

    @Test
    void saveUser_shouldCallRepositoryAndReturnUser() {
        // Arrange
        when(userReactiveRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        // Act
        Mono<User> result = adapter.saveUser(userDomain);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals(userDomain.getEmail()))
                .verifyComplete();
    }

}