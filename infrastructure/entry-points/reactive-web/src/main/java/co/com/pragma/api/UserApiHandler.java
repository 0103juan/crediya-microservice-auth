package co.com.pragma.api;

import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.UserResponse;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@AllArgsConstructor
@Component
public class UserApiHandler {
    private final RegisterUserUseCase registerUserUseCase;
    private final UserMapper userMapper;

    public Mono<ServerResponse> listenRegisterUser(ServerRequest serverRequest) {
        log.info("Recibida petición para registrar nuevo usuario en la ruta: {}", serverRequest.path());
        return serverRequest.bodyToMono(RegisterUserRequest.class)
                .flatMap(user -> {
                    log.info("Petición válida, invocando caso de uso RegisterUserUseCase.");
                    return registerUserUseCase.saveUser(userMapper.toModel(user));
                })
                .flatMap(response -> ServerResponse.ok().bodyValue(response));
    }

    private Mono<UserResponse> registerUserMock() { // TODO: Remove this mock method
        return Mono.fromSupplier(() -> {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            try {
                return mapper.readValue("{\r\n  \"apellidos\" : \"Pérez Gómez\",\r\n  \"id\" : \"c7a7e9a0-3a5e-4f1b-8f0a-9a8b7c6d5e4f\",\r\n  \"email\" : \"juan.perez@example.com\",\r\n  \"nombres\" : \"Juan Alberto\"\r\n}", UserResponse.class);
            } catch (Exception e) {
                throw new RuntimeException("Cannot parse example to UserResponse");
            }
        });
    }
}
