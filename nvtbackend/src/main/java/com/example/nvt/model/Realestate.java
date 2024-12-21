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
    private Client client;

    @OneToMany
    private List<Household> households;

    @ElementCollection
    @CollectionTable(name = "proof_realestate_img", joinColumns = @JoinColumn(name = "realestate_id"))
    @Column(name = "proof_img_url")
    private List<String> proof_images;

    @ElementCollection
    @CollectionTable(name = "realestate_img", joinColumns = @JoinColumn(name = "realestate_id"))
    @Column(name = "img_url")
    private List<String> images;

    @ElementCollection
    @CollectionTable(name = "realestate_pdf", joinColumns = @JoinColumn(name = "realestate_id"))
    @Column(name = "proof_pdf_url")
    private List<String> proof_pdfs;

    private Double lat;
    private Double lon;

    private RealEstateType type;

    private String address;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;
    private Double totalFloors;


}
