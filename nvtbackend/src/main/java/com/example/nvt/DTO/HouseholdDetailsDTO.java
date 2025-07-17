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

    private UserSummaryDTO user;

    private String addressStreet;
    private String addressNum;
    private String city;
    private String municipality;
    private String region;
    private String realestateType;
    private Double lat;
    private Double lon;
    private Long totalFloors;
    private List<String> images;


    private Long apartmentNum;
    private Double size;


}
