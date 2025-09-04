package co.com.pragma.api;

import co.com.pragma.api.config.GlobalExceptionHandler;
import co.com.pragma.api.request.LoginRequest;
import co.com.pragma.api.response.ApiResponse;
import co.com.pragma.api.response.CustomStatus;
import co.com.pragma.api.response.LoginResponse;
import co.com.pragma.api.security.JwtProvider;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.user.User;
import co.com.pragma.usecase.login.LoginUseCase;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {
        LoginApiRouter.class,
        LoginApiHandler.class,
        GlobalExceptionHandler.class, 
        LoginApiHandlerTest.TestConfig.class,
        LoginApiHandlerTest.TestSecurityConfig.class
})
@WebFluxTest
@TestPropertySource(properties = {"jwt.secret=test-secret-key-for-testing-purposes-123456", "jwt.expiration=3600"})
class LoginApiHandlerTest {

    @TestConfiguration
    @EnableWebFluxSecurity
    static class TestSecurityConfig {
        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            http
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            .pathMatchers("/api/v1/login").permitAll()
                            .anyExchange().authenticated()
                    );
            return http.build();
        }
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public LoginUseCase loginUseCase() {
            return Mockito.mock(LoginUseCase.class);
        }

        @Bean
        public JwtProvider jwtProvider() {
            return Mockito.mock(JwtProvider.class);
        }
    }

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private LoginUseCase loginUseCase;

    @Autowired
    private JwtProvider jwtProviderTest;

    private LoginRequest loginRequest;
    private User userDomain;

    @BeforeEach
    void setUp() {
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password");

        userDomain = User.builder().email("test@example.com").build();
    }

    @Test
    void login_whenCredentialsAreValid_shouldReturnOkAndToken() {
        when(loginUseCase.login("test@example.com", "password")).thenReturn(Mono.just(userDomain));
        when(jwtProviderTest.generateToken(any(User.class))).thenReturn("fake-jwt-token");
        CustomStatus expectedStatus = CustomStatus.LOGIN_SUCCESSFUL;

        webTestClient
                .post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<ApiResponse<LoginResponse>>() {})
                .value(response -> {
                    assertThat(response.getCode()).isEqualTo(expectedStatus.getCode());
                    assertThat(response.getData()).isNotNull();
                    assertThat(response.getData().getToken()).isEqualTo("fake-jwt-token");
                });
    }

    @Test
    void login_whenCredentialsAreInvalid_shouldReturnUnauthorized() {
        when(loginUseCase.login("test@example.com", "password"))
                .thenReturn(Mono.error(new InvalidCredentialsException("Credenciales inválidas")));

        webTestClient
                .post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}