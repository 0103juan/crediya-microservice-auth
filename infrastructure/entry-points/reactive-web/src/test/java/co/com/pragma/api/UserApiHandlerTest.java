package co.com.pragma.api;

import co.com.pragma.api.config.SecurityConfig;
import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.UserResponse;
import co.com.pragma.model.user.User;
import co.com.pragma.requestvalidator.RequestValidator;
import co.com.pragma.usecase.finduser.FindUserUseCase;
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

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

        // 1. AÑADIMOS EL BEAN FALTANTE PARA FindUserUseCase
        @Bean
        public FindUserUseCase findUserUseCase() {
            return Mockito.mock(FindUserUseCase.class);
        }

        @Bean
        public RequestValidator requestValidator() {
            return Mockito.mock(RequestValidator.class);
        }

        @Bean
        public UserMapper userMapper() {
            return Mappers.getMapper(UserMapper.class);
        }

        // 2. COMPLETAMOS LAS RUTAS PARA CUBRIR TODOS LOS ENDPOINTS
        @Bean
        public UserPath userPath() {
            UserPath userPath = new UserPath();
            userPath.setUsers("/api/v1/users");
            userPath.setUsersById("/api/v1/users/{idNumber}");
            userPath.setUsersByEmail("/api/v1/users/email/{email}");
            return userPath;
        }
    }

    @Autowired
    private RegisterUserUseCase registerUserUseCase;
    @Autowired
    private FindUserUseCase findUserUseCase;
    @Autowired
    private RequestValidator requestValidator;
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
        validRequest.setIdNumber("98765432");
        validRequest.setPhone(3219876543L);
        validRequest.setBaseSalary(3000000.0);
        validRequest.setBirthDate(LocalDate.of(2000, 1, 1));
        validRequest.setAddress("Avenida Siempre Viva 742");
        validRequest.setRole("ROLE_CLIENTE");

        // Usamos el mapper real que está en el contexto
        userDomain = Mappers.getMapper(UserMapper.class).toModel(validRequest);
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
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
                });
    }

    // 3. NUEVO TEST PARA PROBAR LA BÚSQUEDA POR ID
    @Test
    void findByIdNumber_whenUserExists_shouldReturnOk() {
        // Arrange
        when(findUserUseCase.findByIdNumber("98765432")).thenReturn(Mono.just(userDomain));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/98765432")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getIdNumber()).isEqualTo("98765432");
                });
    }

    // 4. NUEVO TEST PARA PROBAR LA BÚSQUEDA POR EMAIL
    @Test
    void findByEmail_whenUserExists_shouldReturnOk() {
        // Arrange
        when(findUserUseCase.findByEmail("jane.doe@example.com")).thenReturn(Mono.just(userDomain));

        // Act & Assert
        webTestClient.get()
                .uri("/api/v1/users/email/jane.doe@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserResponse.class)
                .value(response -> {
                    assertThat(response.getEmail()).isEqualTo("jane.doe@example.com");
                });
    }
}