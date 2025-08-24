package co.com.pragma.api.mapper;


import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.api.request.RegisterUserRequest;
import co.com.pragma.model.exceptions.InvalidRoleException;
import co.com.pragma.model.role.Role;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel= "spring")
public interface UserMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toListDTO(List<User> users);

    User toModel(RegisterUserRequest registerUserRequest);

    default Role toRole(String roleName) {
        try {
            return Role.valueOf(roleName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidRoleException("El rol '" + roleName + "' no es válido.");
        }
    }
}
