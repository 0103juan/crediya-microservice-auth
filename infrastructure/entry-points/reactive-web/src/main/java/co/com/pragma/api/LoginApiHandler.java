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

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@RequiredArgsConstructor
public class LoginApiHandler {

    private final LoginUseCase loginUseCase;
    private final JwtProvider jwtProvider;

    public Mono<ServerResponse> login(ServerRequest serverRequest) {
        log.info("Recibida petición de login");
        return serverRequest.bodyToMono(LoginRequest.class)
                .flatMap(request -> loginUseCase.login(request.getEmail(), request.getPassword())
                        .flatMap(userDomain -> {
                            String token = jwtProvider.generateToken(userDomain);
                            return ServerResponse.ok()
                                    .contentType(APPLICATION_JSON)
                                    .bodyValue(new LoginResponse(token));
                        })
                        .onErrorResume(InvalidCredentialsException.class, error -> {
                            log.error("Error en el login: {}", error.getMessage());
                            return ServerResponse.status(401).build();
                        })
                        .onErrorResume(Exception.class, error -> {
                            log.error("Error inesperado en el login: ", error);
                            return ServerResponse.status(500).build();
                        })
                );
    }
}