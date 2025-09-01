package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    String,
    UserReactiveRepository
> implements UserRepository {
    private final TransactionalOperator transactionalOperator;

    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        super(repository, mapper, d -> mapper.map(d, User.class));
        this.transactionalOperator = transactionalOperator;
    }

    @Override
    public Mono<User> saveUser(User user) {
        log.info("Iniciando operación de guardado para el usuario con email: {}", user.getEmail());

        return super.save(user)
                .doOnSuccess(savedUser ->
                        log.info("Entidad de usuario guardada exitosamente en la base de datos."))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Buscando usuario por email en la base de datos: {}", email);
        return repository.findByEmail(email)
                .map(this::toEntity);
    }

    @Override
    public Mono<User> findByIdNumber(String idNumber) {
        log.debug("Buscando usuario por número de documento en la base de datos: {}", idNumber);
        return repository.findByIdNumber(idNumber)
                .map(this::toEntity);
    }

    @Override
    public Mono<Boolean> existsByEmailOrIdNumber(String email, String idNumber) {
        log.debug("Buscando usuario por email ó número de documento en la base de datos.");
        return repository.existsByEmailOrIdNumber(email, idNumber);
    }


}
