package com.example.nvt.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Verification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "verification_code", length = 64)
    private String verificationCode;

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate;


    @OneToOne(mappedBy = "verification") // Inverse side
    private User user;

    public Verification(String verificationCode, LocalDateTime expirationDate) {
        this.verificationCode = verificationCode;
        this.expirationDate = expirationDate;
    }

}

