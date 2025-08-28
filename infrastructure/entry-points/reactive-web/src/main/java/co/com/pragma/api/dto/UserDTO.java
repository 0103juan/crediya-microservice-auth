package co.com.pragma.api.dto;

import co.com.pragma.model.role.Role;

import java.time.LocalDate;

public record UserDTO(
        String firstName, String lastName, String email, String idNumber, Long phone, Double baseSalary, LocalDate birthDate, String address, Role role) {
}
