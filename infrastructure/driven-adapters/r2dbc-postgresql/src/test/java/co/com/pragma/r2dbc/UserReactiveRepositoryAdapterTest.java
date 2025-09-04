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
import org.springframework.transaction.reactive.TransactionalOperator;
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
    private TransactionalOperator transactionalOperator;

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
        
        doReturn(userEntity).when(objectMapper).map(userDomain, UserEntity.class);
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.save(any(UserEntity.class))).thenReturn(Mono.just(userEntity));

        when(transactionalOperator.transactional(any(Mono.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        
        Mono<User> result = adapter.save(userDomain);

        
        StepVerifier.create(result)
                .expectNextMatches(savedUser -> savedUser.getEmail().equals(userDomain.getEmail()))
                .verifyComplete();
    }

    @Test
    void findByEmail_shouldReturnUser() {
        
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.findByEmail("test@example.com")).thenReturn(Mono.just(userEntity));

        
        Mono<User> result = adapter.findByEmail("test@example.com");

        
        StepVerifier.create(result)
                .expectNextMatches(foundUser -> foundUser.getEmail().equals(userDomain.getEmail()))
                .verifyComplete();
    }

    @Test
    void findByIdNumber_shouldReturnUser() {
        
        userDomain.setIdNumber("12345");
        userEntity.setIdNumber("12345");
        doReturn(userDomain).when(objectMapper).map(userEntity, User.class);
        when(userReactiveRepository.findByIdNumber("12345")).thenReturn(Mono.just(userEntity));

        
        Mono<User> result = adapter.findByIdNumber("12345");

        
        StepVerifier.create(result)
                .expectNextMatches(foundUser -> foundUser.getIdNumber().equals(userDomain.getIdNumber()))
                .verifyComplete();
    }
}