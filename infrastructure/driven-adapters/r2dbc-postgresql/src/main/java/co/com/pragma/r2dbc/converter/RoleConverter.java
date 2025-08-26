package co.com.pragma.r2dbc.converter;

import co.com.pragma.model.role.Role;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;

public class RoleConverter {

    @WritingConverter
    public static class RoleToIntegerConverter implements Converter<Role, Integer> {
        @Override
        public Integer convert(Role source) {
            return source.getId();
        }
    }

    @ReadingConverter
    public static class IntegerToRoleConverter implements Converter<Integer, Role> {
        @Override
        public Role convert(Integer source) {
            return Role.of(source);
        }
    }
}