package co.com.pragma.r2dbc.entity;


import co.com.pragma.model.role.Role;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigInteger;
import java.time.LocalDate;

@Table("users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String idNumber;
    private Long phone;
    private Double baseSalary;
    private LocalDate birthDate;
    private String address;

    @Column("id_role")
    private Role role;

}
