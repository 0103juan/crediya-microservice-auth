package co.com.pragma.r2dbc;

import co.com.pragma.model.user.User;
import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.r2dbc.entity.UserEntity;
import co.com.pragma.model.exceptions.EmailAlreadyExistsException;
import co.com.pragma.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UserReactiveRepositoryAdapter extends ReactiveAdapterOperations<
    User,
    UserEntity,
    String,
    UserReactiveRepository
> implements UserRepository {
    public UserReactiveRepositoryAdapter(UserReactiveRepository repository, ObjectMapper mapper) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, User.class));
    }

    @Override
    @Transactional
    public Mono<User> saveUser(User user) {
        log.info("Iniciando operación de guardado para el usuario con email: {}", user.getEmail());

        // Simplemente se llama a super.save() con el objeto de dominio.
        // La clase ReactiveAdapterOperations se encargará de la conversión a UserEntity.
        return super.save(user)
                .doOnSuccess(savedUser ->
                        log.info("Entidad de usuario guardada exitosamente en la base de datos."));
        // Ya no es necesario mapear la respuesta, porque super.save() ya devuelve un Mono<User>.
    }

    @Override
    public Mono<User> findByEmail(String email) {
        log.debug("Buscando usuario por email en la base de datos: {}", email);
        // Llamamos al metodo del repositorio Spring Data (devuelve Mono<UserEntity>).
        return repository.findByEmail(email)
                .map(this::toEntity); // Mapeamos el resultado (UserEntity) al modelo de dominio (User).
    }


}
