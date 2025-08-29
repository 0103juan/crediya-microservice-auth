package co.com.pragma.api;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class LoginApiRouter {

    private static final String API_V_1_LOGIN = "/api/v1/login";

    @Bean
    public RouterFunction<ServerResponse> authRoutes(LoginApiHandler loginApiHandler) {
        return route(POST(API_V_1_LOGIN), loginApiHandler::login);
    }
}