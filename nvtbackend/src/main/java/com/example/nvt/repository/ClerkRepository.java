package com.example.nvt.repository;

import com.example.nvt.model.Clerk;
import com.example.nvt.model.Household;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClerkRepository  extends JpaRepository<Clerk, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT c FROM Clerk c WHERE c.id = :clerkId")
    Optional<Clerk> findByIdForUpdate(Long clerkId);
}
