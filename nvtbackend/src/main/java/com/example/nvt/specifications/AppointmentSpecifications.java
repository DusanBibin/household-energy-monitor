package com.example.nvt.specifications;

import com.example.nvt.model.Appointment;
import com.example.nvt.model.HouseholdRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class AppointmentSpecifications {
    public static Specification<Appointment> hasClientId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get("client").get("id"), clientId);
    }

    public static Specification<Appointment> isInTheFuture() {
        return (root, query, cb) -> cb.greaterThan(root.get("startDateTime"), LocalDateTime.now());
    }

}
