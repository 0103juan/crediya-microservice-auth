package co.com.pragma.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración de seguridad para los endpoints de la aplicación.
 * Permite definir qué rutas son públicas y cuáles requieren autenticación.
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // Deshabilitamos la protección CSRF porque es una API stateless que usará tokens.
                .csrf(ServerHttpSecurity.CsrfSpec::disable)

                // Definimos las reglas de autorización para los endpoints.
                .authorizeExchange(exchange -> exchange

                        // 1. PERMITIMOS el acceso sin autenticación a la ruta POST para registrar usuarios.
                        .pathMatchers(HttpMethod.POST, "/api/v1/users").permitAll()

                        // 2. REQUERIMOS autenticación para cualquier otra petición.
                        .anyExchange().authenticated()
                )
                .build();
    }
}