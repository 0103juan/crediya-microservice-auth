package co.com.pragma.usecase.login;

import co.com.pragma.model.exceptions.InvalidCredentialsException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncryptor;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoginUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public Mono<User> login(String email, String rawPassword) {
        return userRepository.findByEmail(email)
                .filter(user -> passwordEncryptor.matches(rawPassword, user.getPassword()))
                .switchIfEmpty(Mono.error(new InvalidCredentialsException("Credenciales inválidas")));
    }
}
