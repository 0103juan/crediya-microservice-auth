package co.com.pragma.config;

import co.com.pragma.model.user.gateways.PasswordEncryptor;
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

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner();

    @Configuration
    static class TestConfig {
        @Bean
        public UserRepository userRepository() {
            return mock(UserRepository.class);
        }
        @Bean
        public PasswordEncryptor passwordEncryptor() {
            return mock(PasswordEncryptor.class);
        }
    }

    @Test
    void useCaseBeansShouldBeCreated() {
        contextRunner.withUserConfiguration(UseCasesConfig.class, TestConfig.class)
                .run(context -> {
                    assertThat(context).hasBean("registerUserUseCase");
                    assertThat(context).hasBean("findUserUseCase");

                    RegisterUserUseCase registerUserUseCase = context.getBean(RegisterUserUseCase.class);
                    FindUserUseCase findUserUseCase = context.getBean(FindUserUseCase.class);

                    assertThat(registerUserUseCase).isNotNull();
                    assertThat(findUserUseCase).isNotNull();

                    assertThat(context).doesNotHaveBean("myUseCase");
                });
    }

    @Test
    void useCaseBeansShouldNotBeCreatedWithoutDependencies() {
        contextRunner.withUserConfiguration(UseCasesConfig.class)
                .run(context -> {
                    assertThat(context).hasFailed();
                    assertThat(context).getFailure().hasCauseInstanceOf(NoSuchBeanDefinitionException.class);
                });
    }
}