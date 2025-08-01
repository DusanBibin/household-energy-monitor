package com.example.nvt.controller;


import com.example.nvt.DTO.AppointmentDTO;
import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.PartialUserDataDTO;
import com.example.nvt.model.Client;
import com.example.nvt.service.AppointmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/clerk/{clerkId}/appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@AuthenticationPrincipal Client client, @PathVariable Long clerkId,
                                                            @RequestParam String startDateTime){


        AppointmentDTO appointment = appointmentService.createAppointment(client, clerkId, startDateTime);
        return ResponseEntity.ok(appointment);
    }


    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/appointment")
    public ResponseEntity<Page<AppointmentDTO>> getClientAppointments(@AuthenticationPrincipal Client client,
                                                                      @RequestParam(defaultValue = "0") int page,
                                                                      @RequestParam(defaultValue = "10") int size){
        Page<AppointmentDTO> appointments = appointmentService.getClientAppointments(client.getId(), page, size);
        return ResponseEntity.ok(appointments);
    }


}
