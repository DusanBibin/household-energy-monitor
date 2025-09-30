package com.example.nvt.controller;


import com.example.nvt.DTO.AppointmentDTO;
import com.example.nvt.DTO.AuthRequestDTO;
import com.example.nvt.DTO.PartialUserDataDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Client;
import com.example.nvt.model.User;
import com.example.nvt.service.AppointmentService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import okhttp3.Response;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PreAuthorize("hasAuthority('CLIENT')")
    @PostMapping("/clerk/{clerkId}/appointment")
    public ResponseEntity<AppointmentDTO> createAppointment(@AuthenticationPrincipal Client client, @PathVariable Long clerkId,
                                                            @RequestParam String startDateTime){

        LocalDateTime start;
        try {
            start = LocalDateTime.parse(startDateTime, DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm"));
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Use dd/MM/yyyy-HH:mm");
        }


        AppointmentDTO appointment = appointmentService.createAppointment(client.getId(), clerkId, start);
        System.out.println("Klijent sa id: " + client.getId() + " je uzeo appointment za " + appointment.getStartDateTime());
        return ResponseEntity.ok(appointment);
    }



    @PreAuthorize("hasAnyAuthority('CLIENT','CLERK')")
    @GetMapping("/appointment")
    public ResponseEntity<List<AppointmentDTO>> getWeekAppointments(@AuthenticationPrincipal User user, @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime){
        List<AppointmentDTO> appointments = appointmentService.getWeekAppointments(user.getId(), startDateTime, endDateTime);
        return ResponseEntity.ok(appointments);
    }


    @PreAuthorize("hasAuthority('CLIENT')")
    @GetMapping("/clerk/{clerkId}/appointment")
    public ResponseEntity<List<AppointmentDTO>> getWeekAppointmentsClerk(@AuthenticationPrincipal Client client,
                                                                         @PathVariable Long clerkId,
                                                                         @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDateTime,
                                                                    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDateTime){
        List<AppointmentDTO> appointments = appointmentService.getWeekAppointmentsClerk(clerkId, startDateTime, endDateTime);
        return ResponseEntity.ok(appointments);
    }

}
