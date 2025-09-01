package co.com.pragma.api.dto;

import co.com.pragma.model.role.Role;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserDTO(
        String firstName, String lastName, String email, String idNumber, Long phone, BigDecimal baseSalary, LocalDate birthDate, String address, Role role) {
}
