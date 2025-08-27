package co.com.pragma.usecase.registeruser;

import co.com.pragma.model.exceptions.IdNumberAlreadyExistsException;
import co.com.pragma.model.user.User;
import co.com.pragma.model.exceptions.EmailAlreadyExistsException;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class RegisterUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> saveUser(User user) {
        Mono<Boolean> emailExists = userRepository.findByEmail(user.getEmail()).hasElement();
        Mono<Boolean> idNumberExists = userRepository.findByIdNumber(user.getIdNumber()).hasElement();

        return Mono.zip(emailExists, idNumberExists)
                .flatMap(tuple -> {
                    boolean emailTaken = tuple.getT1();
                    boolean idTaken = tuple.getT2();

                    if (emailTaken) {
                        return Mono.error(new EmailAlreadyExistsException("El correo electrónico ya está registrado."));
                    }
                    if (idTaken) {
                        return Mono.error(new IdNumberAlreadyExistsException("El número de documento ya está registrado."));
                    }

                    return userRepository.saveUser(user);
                });
    }
}
