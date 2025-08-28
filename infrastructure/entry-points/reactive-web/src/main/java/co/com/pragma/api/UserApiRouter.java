package co.com.pragma.api;

import co.com.pragma.api.config.UserPath;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@RequiredArgsConstructor
@Configuration
public class UserApiRouter {

    private final UserPath userPath;
    private final UserApiHandler userApiHandler;

    @Bean
    public RouterFunction<ServerResponse> routerFunction() {
        return route(POST(userPath.getUsers()), userApiHandler::listenRegister)
                .andRoute(GET(userPath.getUsersById()), userApiHandler::listenFindByIdNumber)
                .andRoute(GET(userPath.getUsersByEmail()), userApiHandler::listenFindByEmail);
    }
}
