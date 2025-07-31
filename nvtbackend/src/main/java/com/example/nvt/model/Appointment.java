package com.example.nvt.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
public class Appointment {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Clerk clerk;

    @ManyToOne
    private Client client;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private boolean isPrivate;
}
