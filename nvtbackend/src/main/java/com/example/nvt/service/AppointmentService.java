package com.example.nvt.service;


import com.example.nvt.exceptions.InvalidInputException;
import com.example.nvt.model.Appointment;
import com.example.nvt.model.Clerk;
import com.example.nvt.model.Client;
import com.example.nvt.repository.AppointmentRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final ClientService clientService;
    private final ClerkService clerkService;
    private final AppointmentRepository appointmentRepository;

    public void createAppointment(Client client, Long clerkId, String dateString, String startTimeString) {
        client = clientService.findClientById(client.getId());


        LocalDate date = LocalDate.now();
        try{
            date = LocalDate.parse(dateString); // Expects "YYYY-MM-DD"
        }catch (DateTimeParseException e){
            throw new InvalidInputException("Invalid date format");
        }

        LocalTime startTime = LocalTime.now();
        try{
            startTime = LocalTime.parse(startTimeString); // Expects "HH:mm"
        }catch (DateTimeParseException e){
            throw new InvalidInputException("Invalid time format");
        }


        if(!isValidAppointmentSlot(startTime)) throw new InvalidInputException("Start time is not valid");
        if(date.isBefore(LocalDate.now())) throw new InvalidInputException("You can only make appointments for the future");


        Clerk clerk = clerkService.getClerkById(clerkId);

        if(appointmentRepository.getExistingAppointment(clerkId, date, startTime).isPresent())
            throw new InvalidInputException("Appointment for this time already exists");


        Appointment appointment = Appointment.builder()
                .clerk(clerk)
                .client(client)
                .date(date)
                .startTime(startTime)
                .endTime(startTime.plusMinutes(30))
                .isPrivate(false)
                .build();

        appointment = saveAppointment(appointment);
        clerk.getAppointments().add(appointment);
        clerk = clerkService.saveClerk(clerk);

        client.getAppointments().add(appointment);
        client = clientService.saveClient(client);

    }

    public Appointment saveAppointment(Appointment appointment){
         return appointmentRepository.save(appointment);
    }

    public boolean isValidAppointmentSlot(LocalTime startTime) {
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

        // Must be on 30-minute boundary (e.g., 08:00, 08:30, etc.)
        if (startTime.getMinute() % 30 != 0 || startTime.getSecond() != 0 || startTime.getNano() != 0) {
            return false;
        }

        return true;
    }

}
