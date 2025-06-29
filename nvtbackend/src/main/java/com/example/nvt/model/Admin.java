package com.example.nvt.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@Entity
//@Table(name = "admin_user")
public class Admin extends User{

    @Override
    public String toString() {
        return "Admin{id=" + this.getId() + ", name=" + this.getEmail() + "}";

    }
}