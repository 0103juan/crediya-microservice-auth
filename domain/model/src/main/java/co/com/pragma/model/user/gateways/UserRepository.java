package co.com.pragma.model.user.gateways;

import co.com.pragma.model.user.User;

import java.util.List;

public interface UserRepository {
    void guardarUser(User User);
    List<User> obtenerTodosLosUsers();
    User obtenerUserPorId(Long idNumber);
    User editarUser(User User);
    void eliminarUser(Long idNumber);
}
