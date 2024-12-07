package com.example.nvt.DTO;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDTO {

    @NotBlank(message = "Name is required")
    String name;

    @NotBlank(message = "Lastname is required")
    String lastname;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    String email;

    @NotBlank(message = "Phone number is required")
    @Size(min = 10,max = 11, message = "Password must be at least 10 characters long")
    @Pattern(
            regexp = "^[0-9]+$",
            message = "Password must contain at least one uppercase letter"
    )
    String phone;

    @NotBlank(message = "Password is required")
    @Size(min = 10, message = "Invalid phone number length")
    @Pattern(
            regexp = ".*[A-Z].*",
            message = "Password must contain at least one uppercase letter"
    )
    @Pattern(
            regexp = ".*[!@#$%^&*(),.?\":{}|<>].*",
            message = "Password must contain at least one special character"
    )
    String password;

    @NotBlank(message = "Repeating password is required")
    String repeatPassword;
}
