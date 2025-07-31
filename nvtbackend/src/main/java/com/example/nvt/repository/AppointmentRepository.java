package com.example.nvt.repository;

import com.example.nvt.model.Appointment;
import com.example.nvt.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {


    @Query("select a from Appointment a where a.clerk.id = :clerkId and a.date = :date and a.startTime = :startTime")
    Optional<Appointment> getExistingAppointment(Long clerkId, LocalDate date, LocalTime startTime);
}
