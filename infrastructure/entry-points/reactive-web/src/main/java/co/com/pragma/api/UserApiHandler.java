package co.com.pragma.api;

import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.UserResponse;
import co.com.pragma.requestvalidator.RequestValidator;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;

@Log4j2
@AllArgsConstructor
@Component
public class UserApiHandler {
    private final RegisterUserUseCase registerUserUseCase;
    private final UserMapper userMapper;
    private final RequestValidator validator;

    public Mono<ServerResponse> listenRegister(ServerRequest serverRequest) {
        log.info("Recibida petición para registrar nuevo usuario en la ruta: {}", serverRequest.path());
        return serverRequest.bodyToMono(RegisterUserRequest.class)
                .flatMap(validator::validate)
                .flatMap(user -> {
                    log.info("Petición válida, invocando caso de uso RegisterUserUseCase.");
                    return registerUserUseCase.saveUser(userMapper.toModel(user));
                })
                .flatMap(savedUser -> {
                    UserResponse userResponse = userMapper.toResponse(savedUser);
                    URI location = URI.create("/api/v1/users/" + savedUser.getIdNumber());
                    return ServerResponse.created(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(userResponse);
                });
    }

}
