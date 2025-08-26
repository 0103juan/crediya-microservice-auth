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
        // Arrange
        userDomain = User.builder()
                .email("test@example.com")
                .firstName("Test")
                .build();

        userEntity = new UserEntity();
        userEntity.setEmail("test@example.com");
        userEntity.setFirstName("Test");

        doReturn(userEntity).when(objectMapper).map(userDomain, UserEntity.class);
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