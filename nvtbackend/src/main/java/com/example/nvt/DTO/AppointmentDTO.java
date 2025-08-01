package com.example.nvt.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentDTO {
    private Long id;
    private UserSummaryDTO clerk;
    private UserSummaryDTO client;
    private LocalDateTime startDateTime;
    private LocalDateTime endDateTime;
    private Boolean isPrivate;


}
