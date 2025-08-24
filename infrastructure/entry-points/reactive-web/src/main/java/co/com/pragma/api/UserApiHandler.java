package co.com.pragma.api;

import co.com.pragma.api.model.UserRequest;
import co.com.pragma.api.model.UserResponse;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Log4j2
@AllArgsConstructor
@Component
public class GestionDeUsersApiHandler {
//    private final UseCase someUseCase;

    public Mono<ServerResponse> registrarUser(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(body -> registrarUserMock()) // TODO: Call real use case here -> someUseCase.some()
                .flatMap(response -> ServerResponse.ok().bodyValue(response)); // TODO: Customize response here
    }

    private Mono<UserResponse> registrarUserMock() { // TODO: Remove this mock method
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
