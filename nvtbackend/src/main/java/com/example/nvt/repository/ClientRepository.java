package com.example.nvt.repository;

import com.example.nvt.model.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Integer> {
    @Query("select c from Client c where c.verification.verificationCode = :code and CURRENT_DATE <= c.verification.expirationDate")
    Optional<Client> findClientByValidValidationCode(String code);
}
