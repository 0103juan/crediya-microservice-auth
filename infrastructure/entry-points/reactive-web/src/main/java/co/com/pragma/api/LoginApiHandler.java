package co.com.pragma.api;

import co.com.pragma.api.request.LoginRequest;
import co.com.pragma.api.response.LoginResponse;
import co.com.pragma.api.security.JwtProvider;
import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.usecase.login.LoginUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginApiHandler {

    private final LoginUseCase loginUseCase;
    private final JwtProvider jwtProvider;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("Recibida petición de login");
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(loginRequest -> loginUseCase.login(loginRequest.getEmail(), loginRequest.getPassword()))
                .flatMap(user -> {
                    String token = jwtProvider.generateToken(user);
                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(new LoginResponse(token));
                })
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas")))
                .doOnSuccess(response -> log.info("Login exitoso"))
                .doOnError(error -> log.error("Error en el login: {}", error.getMessage()));
    }
}