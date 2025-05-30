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


    //realestate data
    private RealEstateType realEstateType;
    private String region;
    private String municipality;
    private String city;
    private String streetName;
    private String addressNumber;
    private Long totalFloors;
    private Double lat;
    private Double lon;

    //household data
    private boolean isVacant;



}
