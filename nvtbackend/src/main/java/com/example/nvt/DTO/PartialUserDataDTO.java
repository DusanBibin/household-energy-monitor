package com.example.nvt.DTO;


import com.example.nvt.enumeration.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class PartialUserDataDTO {
    private Long id;
    private String name;
    private String lastname;
    private String email;
    private Role role;
    private Boolean firstLogin;
}
