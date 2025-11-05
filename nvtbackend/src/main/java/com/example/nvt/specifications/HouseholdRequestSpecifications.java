package com.example.nvt.specifications;

import com.example.nvt.enumeration.RequestStatus;
import com.example.nvt.model.HouseholdRequest;
import org.springframework.data.jpa.domain.Specification;

public class HouseholdRequestSpecifications {
    public static Specification<HouseholdRequest> hasRequesterId(Long clientId) {
        return (root, query, cb) -> cb.equal(root.get("requester").get("id"), clientId);
    }

    public static Specification<HouseholdRequest> hasRequestStatus(RequestStatus status) {
        return (root, query, cb) -> cb.equal(root.get("requestStatus"), status);
    }


    public static Specification<HouseholdRequest> hasHouseholdId(Long householdId) {
        return (root, query, cb) ->
                cb.equal(root.get("household").get("id"), householdId);
    }

    public static Specification<HouseholdRequest> hasStatus(RequestStatus status) {
        return (root, query, cb) ->
                cb.equal(root.get("requestStatus"), status);
    }

    public static Specification<HouseholdRequest> idNotEqual(Long id) {
        return (root, query, cb) ->
                cb.notEqual(root.get("id"), id);
    }

}
