package com.example.nvt.DTO;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SuperadminPasswordChangeDTO {
    @Size(min = 10, message = "Password must be at least 10 characters long")
    @Pattern(
            regexp = ".*[A-Z].*",
            message = "Password must contain at least one uppercase letter"
    )
    @Pattern(
            regexp = ".*[!@#$%^&*(),.?\":{}|<>].*",
            message = "Password must contain at least one special character"
    )
    private String newPassword;

    private String repeatPassword;
}
