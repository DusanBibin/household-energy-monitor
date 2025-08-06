package com.example.nvt.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HouseholdSummaryDTO {
    private Long id;
    private String apartmentNumber;
}
