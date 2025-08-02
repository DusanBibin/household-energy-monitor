package com.example.nvt.model;


import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

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

    private LocalDateTime startDateTime;

    private LocalDateTime endDateTime;

    private boolean isPrivate;
}
