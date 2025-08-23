package co.com.pragma.api;

import co.com.pragma.api.model.ErrorResponse;
import co.com.pragma.api.model.UserRequest;
import co.com.pragma.api.model.UserResponse;
import lombok.extern.log4j.Log4j2;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


import java.util.List;
import java.util.Map;

@Log4j2
@AllArgsConstructor
@Component
public class GestinDeUsuariosApiHandler {
//    private final UseCase someUseCase;

    public Mono<ServerResponse> registrarUsuario(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(UserRequest.class)
                .flatMap(body -> registrarUsuarioMock()) // TODO: Call real use case here -> someUseCase.some()
                .flatMap(response -> ServerResponse.ok().bodyValue(response)); // TODO: Customize response here
    }

    private Mono<UserResponse> registrarUsuarioMock() { // TODO: Remove this mock method
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
