package co.com.pragma.model.user;
import co.com.pragma.model.role.Role;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String idNumber;
    private Long phone;
    private Double baseSalary;
    private Role role;
    private LocalDate birthDate;
    private String address;
}
