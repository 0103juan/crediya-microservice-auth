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
        userDomain = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .build();

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test");
    }

    @Test
    void saveUser_shouldCallRepositoryAndReturnUser() {
        // Arrange
        doReturn(userEntity).when(objectMapper).map(userDomain, UserEntity.class);
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        // Act
        Mono<User> result = adapter.saveUser(userDomain);

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals(userDomain.getEmail()))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        // Arrange
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.findByEmail("test@example.com")).thenReturn(Mono.just(userEntity));

        // Act
        Mono<User> result = adapter.findByEmail("test@example.com");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(foundUser -> foundUser.getEmail().equals(userDomain.getEmail()))
                .verifyComplete();
    }

    @Test
    void findByIdNumber_shouldReturnUser() {
        // Arrange
        userDomain.setIdNumber("12345");
        userEntity.setIdNumber("12345");
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.findByIdNumber("12345")).thenReturn(Mono.just(userEntity));

        // Act
        Mono<User> result = adapter.findByIdNumber("12345");

        // Assert
        StepVerifier.create(result)
                .expectNextMatches(foundUser -> foundUser.getIdNumber().equals(userDomain.getIdNumber()))
                .verifyComplete();
    }
}