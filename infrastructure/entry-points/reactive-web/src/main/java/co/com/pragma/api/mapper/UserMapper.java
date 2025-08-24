package co.com.pragma.api.mapper;


import co.com.pragma.api.dto.RegisterUserDTO;
import co.com.pragma.api.dto.UserDTO;
import co.com.pragma.model.user.User;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel= "spring")
public interface UserDTOMapper {

    UserDTO toDTO(User user);

    List<UserDTO> toListDTO(List<User> users);

    User toModel(RegisterUserDTO registerUserDTO);
}
