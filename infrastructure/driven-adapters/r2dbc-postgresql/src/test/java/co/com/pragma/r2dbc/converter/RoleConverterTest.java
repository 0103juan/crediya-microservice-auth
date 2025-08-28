package co.com.pragma.r2dbc.converter;

import co.com.pragma.model.role.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleConverterTest {

    private final RoleConverter.RoleToIntegerConverter roleToIntegerConverter = new RoleConverter.RoleToIntegerConverter();
    private final RoleConverter.IntegerToRoleConverter integerToRoleConverter = new RoleConverter.IntegerToRoleConverter();

    @Test
    void shouldConvertRoleToInteger() {
        // Arrange
        Role role = Role.ROLE_ADMIN;

        // Act
        Integer roleId = roleToIntegerConverter.convert(role);

        // Assert
        assertEquals(1, roleId);
    }

    @Test
    void shouldConvertIntegerToRole() {
        // Arrange
        Integer roleId = 2;

        // Act
        Role role = integerToRoleConverter.convert(roleId);

        // Assert
        assertEquals(Role.ROLE_CLIENTE, role);
    }
}