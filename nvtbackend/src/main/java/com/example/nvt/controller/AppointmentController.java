package com.example.nvt.controller;


import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.PartialUserDataDTO;
import com.example.nvt.model.Client;
import com.example.nvt.service.AppointmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;

@RestController
@RequestMapping("/api/v1/clerk")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/{clerkId}/appointment")
    public void createAppointment(@AuthenticationPrincipal Client client, @PathVariable Long clerkId,
                                  @RequestParam String date,
                                  @RequestParam String startTime){


        appointmentService.createAppointment(client, clerkId, date, startTime);
    }





}
