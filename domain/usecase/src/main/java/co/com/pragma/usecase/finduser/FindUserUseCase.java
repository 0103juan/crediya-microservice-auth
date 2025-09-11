package co.com.pragma.usecase.finduser;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@RequiredArgsConstructor
public class FindUserUseCase {

    private final UserRepository userRepository;

    public Mono<User> findByIdNumber(String idNumber) {
        return userRepository.findByIdNumber(idNumber);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<Boolean> existsByEmailOrIdNumber(String email, String idNumber){
        return userRepository.existsByEmailOrIdNumber(email, idNumber);
    }

    public Flux<User> findAllByEmails(List<String> emails) {
        return userRepository.findAllByEmails(emails);
    }
}