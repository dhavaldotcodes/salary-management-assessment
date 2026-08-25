package com.example.demo.employee;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public final class EmployeeSpecifications {

    private EmployeeSpecifications() {
    }

    public static Specification<Employee> from(EmployeeQuery query) {
        return (root, criteriaQuery, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (query.search() != null && !query.search().isBlank()) {
                String like = "%" + query.search().trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("firstName")), like),
                        cb.like(cb.lower(root.get("lastName")), like),
                        cb.like(cb.lower(root.get("email")), like),
                        cb.like(cb.lower(root.get("employeeCode")), like)
                ));
            }
            if (hasText(query.country())) {
                predicates.add(cb.equal(root.get("country"), query.country().trim().toUpperCase()));
            }
            if (hasText(query.department())) {
                predicates.add(cb.equal(root.get("department"), query.department().trim()));
            }
            if (hasText(query.jobLevel())) {
                predicates.add(cb.equal(root.get("jobLevel"), query.jobLevel().trim().toUpperCase()));
            }
            if (query.status() != null) {
                predicates.add(cb.equal(root.get("status"), query.status()));
            }
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
