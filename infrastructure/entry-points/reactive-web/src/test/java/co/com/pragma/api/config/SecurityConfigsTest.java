package co.com.pragma.api.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@WebFluxTest
@ContextConfiguration(classes = {SecurityConfig.class, SecurityHeadersConfig.class, SecurityConfigsTest.TestController.class})
class SecurityConfigsTest {

    @Autowired
    private WebTestClient webTestClient;

    // Un controlador de prueba para tener endpoints a los cuales apuntar.
    @RestController
    static class TestController {
        @GetMapping("/test-secured")
        public Mono<String> securedEndpoint() {
            return Mono.just("secured ok");
        }

        @PostMapping("/api/v1/users")
        public Mono<String> publicPostEndpoint() {
            return Mono.just("public post ok");
        }

        @GetMapping("/api/v1/users/some-id")
        public Mono<String> publicGetEndpoint() {
            return Mono.just("public get ok");
        }
    }

    @Test
    void shouldApplySecurityHeaders() {
        webTestClient.get().uri("/api/v1/users/some-id")
                .exchange()
                .expectStatus().isOk() // El endpoint es público
                .expectHeader().valueEquals("X-Content-Type-Options", "nosniff")
                .expectHeader().exists("Content-Security-Policy")
                .expectHeader().valueMatches("Strict-Transport-Security", "max-age=\\d+;")
                .expectHeader().valueEquals("Cache-Control", "no-store");
    }

    @Test
    void shouldAllowPublicPostEndpoints() {
        // La regla en SecurityConfig permite POST a /api/v1/users.
        // Esperamos un 200 OK porque nuestro TestController lo maneja.
        webTestClient.post().uri("/api/v1/users")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void shouldAllowPublicGetEndpoints() {
        // La regla en SecurityConfig permite GET a /api/v1/users/**.
        webTestClient.get().uri("/api/v1/users/some-id")
                .exchange()
                .expectStatus().isOk();
    }


    @Test
    void shouldDenyAccessToAuthenticatedEndpoints() {
        // El endpoint /test-secured no está en la lista de "permitAll",
        // por lo que cae en la regla `anyExchange().authenticated()`.
        // Una llamada sin autenticación debe ser rechazada con 401 Unauthorized.
        webTestClient.get().uri("/test-secured")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}