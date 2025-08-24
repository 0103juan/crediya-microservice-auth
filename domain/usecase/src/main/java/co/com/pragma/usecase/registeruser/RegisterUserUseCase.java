package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.user.User;
import co.com.pragma.model.exceptions.EmailAlreadyExistsException;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user){
        return  userRepository.findByEmail(user.getEmail())
                .flatMap(existingUser -> {
                    return Mono.error(new EmailAlreadyExistsException("El correo ya está registrado."));
                })
                .switchIfEmpty(userRepository.saveUser(user))
                .cast(User.class);
    }
}
