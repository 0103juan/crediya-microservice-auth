package co.com.pragma.model.user.gateways;

/**
 * Puerto para la encriptación y verificación de contraseñas.
 * Define la abstracción para que el dominio no dependa de una implementación específica.
 */
public interface PasswordEncryptor {
    String encode(String password);
    boolean matches(String rawPassword, String encodedPassword);
}