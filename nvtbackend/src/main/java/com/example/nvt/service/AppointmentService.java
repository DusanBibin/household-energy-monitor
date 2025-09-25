package com.example.nvt.service;


import com.example.nvt.DTO.AppointmentDTO;
import com.example.nvt.DTO.UserSummaryDTO;
import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.*;
import com.example.nvt.repository.AppointmentRepository;
import com.example.nvt.specifications.AppointmentSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy-HH:mm");
    private final ClientService clientService;
    private final ClerkService clerkService;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;

    //CACHE PUT
    @Transactional
    public AppointmentDTO createAppointment(Client client, Long clerkId, String startDateTimeString) {

        Appointment appointment = null;

        LocalDateTime startDateTime;
        try {
            startDateTime = LocalDateTime.parse(startDateTimeString, formatter);
        } catch (DateTimeParseException e) {
            throw new InvalidInputException("Invalid date format. Use dd/MM/yyyy-HH:mm");
        }


        client = clientService.findClientById(client.getId());


        if (!isValidAppointmentSlot(startDateTime)) throw new InvalidInputException("Start time is not valid");
        if (startDateTime.isBefore(LocalDateTime.now().plusDays(1))) {
            throw new InvalidInputException("Appointments must be scheduled at least 24 hours in advance.");
        }


        Clerk clerk = clerkService.getClerkById(clerkId);

        if (appointmentRepository.getExistingAppointmentClerk(clerkId, startDateTime).isPresent())
            throw new InvalidInputException("Appointment for this time for this clerk is already taken");

        if (appointmentRepository.getExistingAppointmentClient(client.getId(), startDateTime).isPresent())
            throw new InvalidInputException("You already have an appointment at this time at different clerk");

        appointment = Appointment.builder()
                .clerk(clerk)
                .client(client)
                .startDateTime(startDateTime)
                .endDateTime(startDateTime.plusMinutes(30))
                .isPrivate(false)
                .build();


        appointment = saveAppointment(appointment);

        clerk.getAppointments().add(appointment);
        clerk = clerkService.saveClerk(clerk);

        client.getAppointments().add(appointment);
        client = clientService.saveClient(client);


        return convertToAppointmentDto(appointment);
    }

    public Appointment saveAppointment(Appointment appointment){

        try {
            return appointmentRepository.saveAndFlush(appointment);
        } catch (DataIntegrityViolationException e) {
            throw new InvalidInputException("This time slot has just been taken by another client");
        }

    }

    public boolean isValidAppointmentSlot(LocalDateTime startDateTime) {
        // Reject weekends
        DayOfWeek day = startDateTime.getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            return false;
        }

        // Extract the time part
        LocalTime startTime = startDateTime.toLocalTime();

        // Define working hours
        LocalTime workStart = LocalTime.of(8, 0);
        LocalTime workEnd = LocalTime.of(16, 0);

        // Define lunch break range
        LocalTime lunchStart = LocalTime.of(12, 0);
        LocalTime lunchEnd = LocalTime.of(12, 30);

        // Must be within working hours
        if (startTime.isBefore(workStart) || startTime.plusMinutes(30).isAfter(workEnd)) {
            return false;
        }

        // Must not start during lunch break
        if (!startTime.isBefore(lunchStart) && startTime.isBefore(lunchEnd)) {
            return false;
        }

        // Must be on a 30-minute boundary
        if (startTime.getMinute() % 30 != 0 || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            return false;
        }

        return true;
    }



    public Page<AppointmentDTO> getClientAppointments(Long clientId, int page, int size) {

        if(page < 0 ) page = 0;
        if(size < 1) size = 10;


        Pageable pageable = PageRequest.of(page, size, Sort.by("startDateTime").ascending());

        Specification<Appointment> spec = Specification
                .where(AppointmentSpecifications.hasClientId(clientId))
                .and(AppointmentSpecifications.isInTheFuture());


        Page<Appointment> resultPage = appointmentRepository.findAll(spec, pageable);

        return resultPage.map(this::convertToAppointmentDto);
    }


    public static boolean isValidWeekRange(LocalDateTime start, LocalDateTime end) {
        // 1. Check if start is before end
        if (!start.isBefore(end)) {
            return false;
        }

        // 2. Check if both are Mondays
        if (start.getDayOfWeek() != DayOfWeek.MONDAY || end.getDayOfWeek() != DayOfWeek.MONDAY) {
            return false;
        }

        // 3. Check if both times are exactly midnight (00:00:00)
        if (!start.toLocalTime().equals(LocalTime.MIDNIGHT) ||
                !end.toLocalTime().equals(LocalTime.MIDNIGHT)) {
            return false;
        }

        // 4. Check if exactly 7 days apart
        long daysBetween = ChronoUnit.DAYS.between(start.toLocalDate(), end.toLocalDate());
        return daysBetween == 7;
    }

    private AppointmentDTO convertToAppointmentDto(Appointment appointment) {

        return AppointmentDTO.builder()
                .id(appointment.getId())
                .clerk(userService.convertToDTO(appointment.getClerk()))
                .client(userService.convertToDTO(appointment.getClient()))
                .startDateTime(appointment.getStartDateTime())
                .endDateTime(appointment.getEndDateTime())
                .isPrivate(appointment.isPrivate())
                .build();
    }


    //CACHABLE
    public List<AppointmentDTO> getWeekAppointments(User user, LocalDateTime startDateTime, LocalDateTime endDateTime) {

        if(!isValidWeekRange(startDateTime, endDateTime)) throw new InvalidInputException("Invalid week range specified");
        List<Appointment> appointments = new ArrayList<>();
        if(user instanceof Client) appointments = appointmentRepository.getWeekAppointmentsClient(user.getId(), startDateTime, endDateTime);
        if(user instanceof Clerk)  appointments = appointmentRepository.getWeekAppointmentsClerk(user.getId(), startDateTime, endDateTime);

        return appointments.stream()
                .map(this::convertToAppointmentDto)
                .toList();
    }


    //CACHABLE
    public List<AppointmentDTO> getWeekAppointmentsClerk(Client client, Long clerkId, LocalDateTime startDateTime,
                                                         LocalDateTime endDateTime) {
        Clerk clerk = clerkService.getClerkById(clerkId);


        if(!isValidWeekRange(startDateTime, endDateTime)) throw new InvalidInputException("Invalid week range specified");
        List<Appointment> appointments = appointmentRepository.getWeekAppointmentsClerk(clerkId, startDateTime, endDateTime);

        return appointments.stream()
                .map(appointment -> {
                    Clerk cle = appointment.getClerk();
                    Client cli = appointment.getClient();

                    return AppointmentDTO.builder()
                        .id(appointment.getId())
                        .clerk(new UserSummaryDTO(cle.getId(), cle.getEmail(), cle.getFirstName(), cle.getLastname()))
                        .client(new UserSummaryDTO(cli.getId(), cli.getEmail(), cli.getFirstName(), cli.getLastname()))
                        .startDateTime(appointment.getStartDateTime())
                        .endDateTime(appointment.getEndDateTime())
                        .build();
                    })

                .toList();
    }
}
