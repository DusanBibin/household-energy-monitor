package com.example.nvt.model;

import com.example.nvt.enumeration.RealEstateType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Realestate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Client realestateOwner;

    @OneToMany(mappedBy = "realestate", cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Household> households;

    @OneToOne
    private RealEstateRequest request;


    @ElementCollection
    @CollectionTable(name = "realestate_img", joinColumns = @JoinColumn(name = "realestate_id"))
    @Column(name = "img_url")
    private List<String> images;

    private Double lat;
    private Double lon;

    private RealEstateType type;

    private String addressStreet;

    private String addressNum;

    private boolean isVacant;


    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private Long totalFloors;
    private Long apartmentPerFloorNum;

}
