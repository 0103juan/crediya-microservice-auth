package co.com.pragma.api.config;

import co.com.pragma.api.security.AuthenticationManager;
import co.com.pragma.api.security.SecurityContextRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange(exchange -> exchange
                        // Endpoints públicos
                        .pathMatchers("/api/v1/login").permitAll()
                        .pathMatchers(
                                "/swagger-ui.html", "/swagger-ui/**",
                                "/v3/api-docs/**", "/webjars/**", "/openapi/**"
                        ).permitAll()
                        .pathMatchers(HttpMethod.POST, "/api/v1/users").hasAnyAuthority("ROLE_ADMIN", "ROLE_ASESOR")
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/**").authenticated()
                        .anyExchange().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}