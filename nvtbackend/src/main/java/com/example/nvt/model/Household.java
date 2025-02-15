package com.example.nvt.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class Household {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Realestate realEstate;

    @ManyToOne
    private Client houseHoldOwner;

    @OneToMany
    private List<Client> authorizedViewers;

    private Boolean isOnline;
    private LocalDateTime lastOnline;

    private Double totalFloors;
    private Double apartmentNum; // popunjava se samo ako je stan u pitanju

    private Double size;
}
