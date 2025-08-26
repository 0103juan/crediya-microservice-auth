package co.com.pragma.api.config;

import co.com.pragma.api.UserApiHandler;
import co.com.pragma.api.UserApiRouter;
import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.requestvalidator.RequestValidator;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

@WebFluxTest
@TestPropertySource(properties = "cors.allowed-origins=http://example.com")
class ConfigTest {

    @SpringBootConfiguration
    @Import({
            UserApiRouter.class,
            UserApiHandler.class,
            CorsConfig.class,
            SecurityHeadersConfig.class,
            SecurityConfig.class
    })
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
    private WebTestClient webTestClient;

    @Test
    void securityHeadersShouldBePresentInResponse() {
        webTestClient
                .options().uri("/api/v1/users")
                .header("Origin", "http://example.com")
                .header("Access-Control-Request-Method", "POST")// Cabecera necesaria para activar CORS
                .exchange()
                .expectStatus().isOk() // Esperamos un 200 OK
                .expectHeader().valueEquals("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'")
                .expectHeader().exists("Strict-Transport-Security")
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().exists("Referrer-Policy");
    }
}