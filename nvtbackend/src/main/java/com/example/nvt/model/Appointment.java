package com.example.nvt.model;


import jakarta.persistence.*;
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
@Table(
        name = "appointment",
        uniqueConstraints = @UniqueConstraint(columnNames = {"clerk_id", "start_date_time"})
)
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
