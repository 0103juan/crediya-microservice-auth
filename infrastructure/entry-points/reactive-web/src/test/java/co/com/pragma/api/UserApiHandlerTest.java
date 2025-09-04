package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.ApiResponse;
import co.com.pragma.api.response.CustomStatus;
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
import org.springframework.boot.autoconfigure.security.reactive.ReactiveSecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@WebFluxTest(excludeAutoConfiguration = {ReactiveSecurityAutoConfiguration.class})
@ContextConfiguration(classes = {UserApiRouter.class, UserApiHandler.class, UserApiHandlerTest.TestConfig.class})
class UserApiHandlerTest {

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RegisterUserUseCase registerUserUseCase() {
            return Mockito.mock(RegisterUserUseCase.class);
        }
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
    private WebTestClient webTestClient;
    @Autowired
    private RegisterUserUseCase registerUserUseCase;
    @Autowired
    private FindUserUseCase findUserUseCase;
    @Autowired
    private RequestValidator requestValidator;

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
        validRequest.setBaseSalary(BigDecimal.valueOf(3000000.0));
        validRequest.setBirthDate(LocalDate.of(2000, 1, 1));
        validRequest.setAddress("Avenida Siempre Viva 742");
        validRequest.setRole("ROLE_CLIENTE");
        userDomain = Mappers.getMapper(UserMapper.class).toModel(validRequest);
    }

    @Test
    void registerUser_whenRequestIsValid_shouldReturnCreated() {
        when(requestValidator.validate(any(RegisterUserRequest.class))).thenReturn(Mono.just(validRequest));
        when(registerUserUseCase.save(any(User.class))).thenReturn(Mono.just(userDomain));
        CustomStatus expectedStatus = CustomStatus.USER_CREATED_SUCCESSFULLY;

        webTestClient.post()
                .uri("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(validRequest)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
                .value(apiResponse -> {
                    assertThat(apiResponse.getCode()).isEqualTo(expectedStatus.getCode());
                    assertThat(apiResponse.getData()).isNotNull();
                    assertThat(apiResponse.getData().getEmail()).isEqualTo("jane.doe@example.com");
                });
    }

    @Test
    void findByIdNumber_whenUserExists_shouldReturnOk() {
        when(findUserUseCase.findByIdNumber("98765432")).thenReturn(Mono.just(userDomain));
        CustomStatus expectedStatus = CustomStatus.USER_FOUND_SUCCESSFULLY;

        webTestClient.get()
                .uri("/api/v1/users/98765432")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
                .value(apiResponse -> {
                    assertThat(apiResponse.getCode()).isEqualTo(expectedStatus.getCode());
                    assertThat(apiResponse.getData()).isNotNull();
                    assertThat(apiResponse.getData().getIdNumber()).isEqualTo("98765432");
                });
    }

    @Test
    void findByEmail_whenUserExists_shouldReturnOk() {
        when(findUserUseCase.findByEmail("jane.doe@example.com")).thenReturn(Mono.just(userDomain));
        CustomStatus expectedStatus = CustomStatus.USER_FOUND_SUCCESSFULLY;

        webTestClient.get()
                .uri("/api/v1/users/email/jane.doe@example.com")
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<UserResponse>>() {})
                .value(apiResponse -> {
                    assertThat(apiResponse.getCode()).isEqualTo(expectedStatus.getCode());
                    assertThat(apiResponse.getData()).isNotNull();
                    assertThat(apiResponse.getData().getEmail()).isEqualTo("jane.doe@example.com");
                });
    }
}