// infrastructure/entry-points/reactive-web/src/test/java/co/com/pragma/api/LoginApiHandlerTest.java
package co.com.pragma.api;

import co.com.pragma.api.request.LoginRequest;
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

@ContextConfiguration(classes = {LoginApiRouter.class, LoginApiHandler.class, LoginApiHandlerTest.TestConfig.class, LoginApiHandlerTest.TestSecurityConfig.class})
@WebFluxTest
@TestPropertySource(properties = {"jwt.secret=test-secret-key-for-testing-purposes-123456", "jwt.expiration=3600"})
class LoginApiHandlerTest {

    // ESTA ES LA NUEVA CLASE DE CONFIGURACIÓN
    @TestConfiguration
    @EnableWebFluxSecurity
    static class TestSecurityConfig {

        @Bean
        public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
            http
                    // Deshabilitamos CSRF para todas las solicitudes en esta prueba
                    .csrf(ServerHttpSecurity.CsrfSpec::disable)
                    // Deshabilitamos la autenticación básica que causa el popup
                    .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                    .authorizeExchange(exchanges -> exchanges
                            // Permitimos el acceso PÚBLICO a nuestro endpoint de login
                            .pathMatchers("/api/v1/login").permitAll()
                            // (Opcional) Aseguramos cualquier otra ruta, por si acaso
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

        webTestClient // Ya no necesitas .mutateWith(csrf())
                .post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isOk() // ¡Ahora sí!
                .expectBody(LoginResponse.class)
                .value(response -> {
                    assertThat(response.getToken()).isEqualTo("fake-jwt-token");
                });
    }

    @Test
    void login_whenCredentialsAreInvalid_shouldReturnUnauthorized() {
        when(loginUseCase.login("test@example.com", "password"))
                .thenReturn(Mono.error(new InvalidCredentialsException("Credenciales inválidas")));

        webTestClient // Ya no necesitas .mutateWith(csrf())
                .post()
                .uri("/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(loginRequest)
                .exchange()
                .expectStatus().isUnauthorized();
    }
}