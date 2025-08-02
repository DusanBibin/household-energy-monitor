package com.example.nvt.repository;

import com.example.nvt.model.Clerk;
import com.example.nvt.model.Household;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClerkRepository  extends JpaRepository<Clerk, Long> {
}
