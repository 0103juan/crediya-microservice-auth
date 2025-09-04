package co.com.pragma.api;

import co.com.pragma.api.mapper.UserMapper;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.api.response.ApiResponse;
import co.com.pragma.api.response.CustomStatus;
import co.com.pragma.api.response.UserResponse;
import co.com.pragma.model.exceptions.UserNotFoundException;
import co.com.pragma.model.user.User;
import co.com.pragma.requestvalidator.RequestValidator;
import co.com.pragma.usecase.finduser.FindUserUseCase;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
    private final FindUserUseCase findUserUseCase;
    private final UserMapper userMapper;
    private final RequestValidator validator;

    public Mono<ServerResponse> listenRegister(ServerRequest serverRequest) {
        log.info("Recibida petición para registrar nuevo usuario en la ruta: {}", serverRequest.path());
        return serverRequest.bodyToMono(RegisterUserRequest.class)
                .flatMap(validator::validate)
                .flatMap(user -> {
                    log.info("Petición válida, invocando caso de uso RegisterUserUseCase.");
                    User userDomain = userMapper.toModel(user);
                    return registerUserUseCase.save(userDomain);
                })
                .flatMap(savedUser -> {
                    UserResponse userResponse = userMapper.toResponse(savedUser);
                    URI location = URI.create("/api/v1/users/" + savedUser.getIdNumber());
                    CustomStatus status = CustomStatus.USER_CREATED_SUCCESSFULLY;

                    ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                            .code(status.getCode())
                            .message(status.getMessage())
                            .path(location.toString())
                            .data(userResponse)
                            .build();

                    return ServerResponse.created(location)
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                });
    }

    public Mono<ServerResponse> listenFindByIdNumber(ServerRequest serverRequest) {
        String idNumber = serverRequest.pathVariable("idNumber");
        log.info("Recibida petición de consulta para el idNumber: {}", idNumber);

        return findUserUseCase.findByIdNumber(idNumber)
                .flatMap(user -> {
                    UserResponse userResponse = userMapper.toResponse(user);
                    CustomStatus status = CustomStatus.USER_FOUND_SUCCESSFULLY;

                    ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                            .code(status.getCode())
                            .message(status.getMessage())
                            .path(serverRequest.path())
                            .data(userResponse)
                            .build();

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con idNumber " + idNumber + " no encontrado.")));
    }

    public Mono<ServerResponse> listenFindByEmail(ServerRequest serverRequest) {
        String email = serverRequest.pathVariable("email");
        log.info("Recibida petición de consulta para el email: {}", email);

        return findUserUseCase.findByEmail(email)
                .flatMap(user -> {
                    UserResponse userResponse = userMapper.toResponse(user);
                    CustomStatus status = CustomStatus.USER_FOUND_SUCCESSFULLY;

                    ApiResponse<UserResponse> apiResponse = ApiResponse.<UserResponse>builder()
                            .code(status.getCode())
                            .message(status.getMessage())
                            .path(serverRequest.path())
                            .data(userResponse)
                            .build();

                    return ServerResponse.ok()
                            .contentType(MediaType.APPLICATION_JSON)
                            .bodyValue(apiResponse);
                })
                .switchIfEmpty(Mono.error(new UserNotFoundException("Usuario con email " + email + " no encontrado.")));
    }
}