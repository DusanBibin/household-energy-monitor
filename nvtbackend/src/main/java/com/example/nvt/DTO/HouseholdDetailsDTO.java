package com.example.nvt.DTO;


import com.example.nvt.enumeration.RealEstateType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdDetailsDTO {


    private List<String> imagePaths;

    private Double lat;
    private Double lon;

    private RealEstateType type;

    private String addressStreet;
    private String addressNumber;
    private Long postalCode;
    private String city;
    private String municipality;
    private String region;

    private Long totalFloors;

    private Double size;

    //ako je zgrada
    private Long apartmentPerFloorNum;
    //ako je kuca imace 1  koji ce biti prikazan, ako je zgrada imace
    private List<Long> apartmentNumSelector;
    private Long apartmentNum;

    //ako ima vlasnika
    private String ownerName;
    private String ownerSurname;
    private String ownerEmail;


}
