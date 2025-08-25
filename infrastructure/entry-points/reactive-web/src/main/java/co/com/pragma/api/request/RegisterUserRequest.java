package co.com.pragma.api.request;


import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
* Datos para registrar un nuevo user en el sistema.
*/
@Data
public class RegisterUserRequest {

    @NotBlank(message = "El nombre no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El nombre solo puede contener letras y espacios.")
    private String firstName;

    @NotBlank(message = "El apellido no puede estar vacío.")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres.")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]+$", message = "El apellido solo puede contener letras y espacios.")
    private String lastName;

    @NotBlank(message = "El correo electrónico no puede estar vacío.")
    @Email(message = "El formato del correo electrónico no es válido.")
    @Size(max = 100, message = "El correo electrónico no puede exceder los 100 caracteres.")
    private String email;

    @NotNull(message = "El número de documento no puede ser nulo.")
    @DecimalMin(value = "1000000", message = "El número de documento debe tener al menos 7 dígitos y/o ser positivo.")
    @DecimalMax(value = "999999999999999", message = "El número de documento debe tener máximo 15 dígitos.")
    private Long idNumber;

    @NotNull(message = "El teléfono no puede ser nulo.")
    @Positive(message = "El teléfono debe ser un número positivo.")
    @Digits(integer = 10, fraction = 0, message = "El número de teléfono debe tener máximo 10 dígitos.")
    private Long phone;

    @NotNull(message = "El salario base no puede ser nulo.")
    @DecimalMin(value = "0.0", inclusive = true, message = "El salario base no puede ser negativo.")
    @DecimalMax(value = "15000000.0", inclusive = true, message = "El salario base no puede exceder 15,000,000.")
    private Double baseSalary;

    @NotNull(message = "La fecha de nacimiento no puede ser nula.")
    @Past(message = "La fecha de nacimiento debe ser una fecha en el pasado.")
    private LocalDate birthDate;

    @NotBlank(message = "La dirección no puede estar vacía.")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres.")
    private String address;

    @NotBlank(message = "El rol no puede estar vacío.")
    private String role;
}