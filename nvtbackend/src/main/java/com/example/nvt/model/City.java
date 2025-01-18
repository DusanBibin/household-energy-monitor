package com.example.nvt.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Long zipCode;

    @ManyToOne
    @JoinColumn(name = "municipality_id")
    private Municipality municipality;

//    @Column(nullable = false)
//    private Double lat;
//    @Column(nullable = false)
//    private Double lon;


}
