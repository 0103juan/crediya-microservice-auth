package co.com.pragma.api.security;

import co.com.pragma.model.user.gateways.PasswordEncryptor;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa el puerto PasswordEncryptor utilizando
 * el PasswordEncoder de Spring Security.
 */
@Component
@RequiredArgsConstructor
public class PasswordEncoderAdapter implements PasswordEncryptor {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}