package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.DuplicateDataException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.PasswordEncryptor;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final PasswordEncryptor passwordEncryptor;

    public Mono<User> saveUser(User userToSave) {
        userToSave.setPassword(passwordEncryptor.encode(userToSave.getPassword()));

        return userRepository.existsByEmailOrIdNumber(userToSave.getEmail(), userToSave.getIdNumber())
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new DuplicateDataException("El correo electrónico o el número de documento ya están registrados."));
                    } else {
                        return userRepository.saveUser(userToSave);
                    }
                });
    }
}
