package com.example.nvt.repository;

import com.example.nvt.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long>, JpaSpecificationExecutor<Appointment> {


    @Query("select a from Appointment a where a.clerk.id = :clerkId and a.startDateTime =:startDateTime")
    Optional<Appointment> getExistingAppointmentClerk(Long clerkId, LocalDateTime startDateTime);

    @Query("select a from Appointment a where a.client.id = :clientId and a.startDateTime =:startDateTime")
    Optional<Appointment> getExistingAppointmentClient(Long clientId, LocalDateTime startDateTime);
}
