package co.com.pragma.api;

import co.com.pragma.api.request.LoginRequest;
import co.com.pragma.api.response.ApiResponse;
import co.com.pragma.api.response.CustomStatus;
import co.com.pragma.api.response.LoginResponse;
import co.com.pragma.api.security.JwtProvider;
import co.com.pragma.usecase.login.LoginUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
                            LoginResponse loginResponse = new LoginResponse(token);
                            CustomStatus status = CustomStatus.LOGIN_SUCCESSFUL;
                            ApiResponse<LoginResponse> apiResponse = ApiResponse.<LoginResponse>builder()
                                    .code(status.getCode())
                                    .message(status.getMessage())
                                    .path(serverRequest.path())
                                    .data(loginResponse)
                                    .build();

                            return ServerResponse.ok()
                                    .contentType(APPLICATION_JSON)
                                    .bodyValue(apiResponse);
                        })
                );
    }
}