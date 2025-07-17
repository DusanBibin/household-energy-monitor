package com.example.nvt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
public class Client extends User{

    @OneToMany
    private List<Realestate> realEstates;

    @OneToMany
    private List<Household> households;

    @OneToMany
    private List<AssetRequest> assetRequests;


    @Override
    public String toString() {
        return "Client{id=" + this.getId() + ", name=" + this.getEmail() + "}";

    }
}