package co.com.pragma.config;

import co.com.pragma.model.user.gateways.UserRepository;
import co.com.pragma.usecase.finduser.FindUserUseCase;
import co.com.pragma.usecase.registeruser.RegisterUserUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UseCasesConfigTest {

    // Se crea un corredor de contexto de aplicación para simular el arranque de Spring.
    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    // Configuración de prueba que provee un mock del UserRepository.
    // Los casos de uso reales necesitan esta dependencia para ser creados.
    @Configuration
    static class TestConfig {
        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
    }

    @Test
    void useCaseBeansShouldBeCreated() {
        // Se ejecuta el contexto con la configuración de UseCases y la de prueba.
        contextRunner.withUserConfiguration(UseCasesConfig.class, TestConfig.class)
                .run(context -> {
                    // Se verifica que los beans de los casos de uso reales existan en el contexto.
                    assertThat(context).hasBean("registerUserUseCase");
                    assertThat(context).hasBean("findUserUseCase");

                    // Se obtienen los beans para asegurar que no son nulos.
                    RegisterUserUseCase registerUserUseCase = context.getBean(RegisterUserUseCase.class);
                    FindUserUseCase findUserUseCase = context.getBean(FindUserUseCase.class);

                    assertThat(registerUserUseCase).isNotNull();
                    assertThat(findUserUseCase).isNotNull();

                    // Opcional: Verificar que no exista un bean que no debería estar.
                    assertThat(context).doesNotHaveBean("myUseCase");
                });
    }

    @Test
    void useCaseBeansShouldNotBeCreatedWithoutDependencies() {
        // Se ejecuta el contexto solo con UseCasesConfig, sin la dependencia UserRepository.
        // Esto simula un error de configuración.
        contextRunner.withUserConfiguration(UseCasesConfig.class)
                .run(context -> {
                    // Se espera que el arranque falle porque falta la dependencia.
                    // Spring no podrá crear los beans de los casos de uso.
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure().hasCauseInstanceOf(NoSuchBeanDefinitionException.class);
                });
    }
}