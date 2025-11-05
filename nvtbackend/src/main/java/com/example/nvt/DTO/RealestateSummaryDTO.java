package com.example.nvt.DTO;


import com.example.nvt.enumeration.RealEstateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RealestateSummaryDTO {
    private Long realestateId;
    private RealEstateType type;
    private List<HouseholdSummaryDTO> householdSummaries;
    private String address;
    private List<String> images;
}
