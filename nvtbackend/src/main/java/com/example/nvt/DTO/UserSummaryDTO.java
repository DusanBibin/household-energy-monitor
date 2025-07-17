package com.example.nvt.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserSummaryDTO {
    private Long id;
    private String email;
    private String name;
    private String lastname;
}
