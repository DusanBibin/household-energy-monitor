package com.example.nvt.DTO;

import com.example.nvt.enumeration.RealEstateType;
import com.example.nvt.enumeration.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class HouseholdRequestPreviewDTO {

    private Long id;
    private Long householdId;
    private Long realestateId;

    private String address;
    private RequestStatus requestStatus;
    private LocalDateTime requestSubmitted;
    private LocalDateTime requestProcessed;
    private RealEstateType realEstateType;
}
