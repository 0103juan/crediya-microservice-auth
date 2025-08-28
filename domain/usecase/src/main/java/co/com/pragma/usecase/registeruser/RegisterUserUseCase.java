package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.DuplicateDataException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User userToSave) {
        return userRepository.existsByEmailOrIdNumber(userToSave.getEmail(), userToSave.getIdNumber())
                .flatMap(exists -> {
                    if (exists) {
                        return Mono.error(new DuplicateDataException("El correo electrónico o el número de documento ya están registrados."));
                    } else {
                        return userRepository.saveUser(userToSave);
                    }
                });
    }
}
