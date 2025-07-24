package com.example.nvt.DTO;


import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.enumeration.RequestType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class HouseholdRequestDTO {

    private Long id;

    private UserSummaryDTO requester;
    private UserSummaryDTO reviewingAdmin;

    private List<String> proof_images;
    private List<String> proof_pdfs;
    private RequestStatus requestStatus;
    private RequestType requestType;
    private LocalDateTime requestSubmitted;
    private LocalDateTime requestProcessed;
    private String denialReason;

    private Long householdId;
    private Long realestateId;
}
