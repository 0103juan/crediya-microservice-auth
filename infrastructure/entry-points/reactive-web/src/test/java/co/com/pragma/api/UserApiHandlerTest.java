package co.com.pragma.api;

import co.com.pragma.api.config.SecurityConfig;
import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.model.user.User;
import co.com.pragma.requestvalidator.RequestValidator;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {UserApiRouter.class, SecurityConfig.class, UserApiHandler.class, UserApiHandlerTest.TestConfig.class})
class UserApiHandlerTest {

    @TestConfiguration
    static class TestConfig {

        @Bean
        public RegisterUserUseCase registerUserUseCase() {
            return Mockito.mock(RegisterUserUseCase.class);
        }

        @Bean
        public RequestValidator requestValidator() {
            return Mockito.mock(RequestValidator.class);
        }

        @Bean
        public UserMapper userMapper() {
            return Mappers.getMapper(UserMapper.class);
        }

        @Bean
        public UserPath userPath() {
            UserPath userPath = new UserPath();
            userPath.setUsers("/api/v1/users");
            return userPath;
        }
    }

    @Autowired
    private RegisterUserUseCase registerUserUseCase;

    @Autowired
    private RequestValidator requestValidator;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private WebTestClient webTestClient;

    private RegisterUserRequest validRequest;
    private User userDomain;


    @BeforeEach
    void setUp() {
        validRequest = new RegisterUserRequest();
        validRequest.setFirstName("Jane");
        validRequest.setLastName("Doe");
        validRequest.setEmail("jane.doe@example.com");
        validRequest.setIdNumber(98765432L);
        validRequest.setPhone(3219876543L);
        validRequest.setBaseSalary(3000000.0);
        validRequest.setBirthDate(LocalDate.of(2000, 1, 1));
        validRequest.setAddress("Avenida Siempre Viva 742");
        validRequest.setRole("ROLE_CLIENTE");

        userDomain = userMapper.toModel(validRequest);
    }

    @Test
    void registerUser_whenRequestIsValid_shouldReturnCreated() {
        // Arrange
        when(requestValidator.validate(any(RegisterUserRequest.class))).thenReturn(Mono.just(validRequest));
        when(registerUserUseCase.saveUser(any(User.class))).thenReturn(Mono.just(userDomain));

        // Act & Assert
        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo("jane.doe@example.com");
    }
}