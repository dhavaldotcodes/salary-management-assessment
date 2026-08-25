package com.example.demo.employee;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
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
                predicates.add(matchesNameOrContact(root, cb, query.search()));
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
            predicates.add(cb.equal(root.get("status"), EmploymentStatus.ACTIVE));
            return cb.and(predicates.toArray(Predicate[]::new));
        };
    }

    private static Predicate matchesNameOrContact(Root<Employee> root, CriteriaBuilder cb, String raw) {
        List<String> tokens = tokens(raw);
        if (tokens.isEmpty()) {
            return cb.conjunction();
        }

        Predicate everyTokenInNameOrContact = cb.conjunction();
        for (String token : tokens) {
            everyTokenInNameOrContact = cb.and(everyTokenInNameOrContact, matchesToken(root, cb, token));
        }

        if (tokens.size() == 1) {
            return everyTokenInNameOrContact;
        }

        String given = tokens.get(0);
        String family = String.join(" ", tokens.subList(1, tokens.size()));
        Predicate firstThenLast = cb.and(
                contains(cb, root.get("firstName"), given),
                contains(cb, root.get("lastName"), family)
        );
        Predicate lastThenFirst = cb.and(
                contains(cb, root.get("firstName"), family),
                contains(cb, root.get("lastName"), given)
        );
        return cb.or(firstThenLast, lastThenFirst, everyTokenInNameOrContact);
    }

    private static Predicate matchesToken(Root<Employee> root, CriteriaBuilder cb, String token) {
        return cb.or(
                contains(cb, root.get("firstName"), token),
                contains(cb, root.get("lastName"), token),
                contains(cb, root.get("email"), token),
                contains(cb, root.get("employeeCode"), token)
        );
    }

    private static Predicate contains(CriteriaBuilder cb, Path<String> path, String token) {
        return cb.like(cb.lower(path), "%" + token + "%");
    }

    static List<String> tokens(String raw) {
        List<String> tokens = new ArrayList<>();
        String normalized = raw.trim().toLowerCase()
                .replace('+', ' ')
                .replace('"', ' ')
                .replace('\'', ' ');
        for (String part : normalized.split("\\s+")) {
            String cleaned = part.replace("%", "").replace("_", "");
            if (!cleaned.isBlank()) {
                tokens.add(cleaned);
            }
        }
        return tokens;
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
