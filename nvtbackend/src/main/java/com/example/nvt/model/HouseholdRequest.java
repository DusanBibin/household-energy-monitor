package com.example.nvt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class HouseholdRequest extends AssetRequest{
    @OneToOne
    private Household household;
}
