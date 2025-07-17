package com.example.nvt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;

@Entity
public class RealestateRequest extends AssetRequest{
    @OneToOne
    private Realestate realestate;
}
