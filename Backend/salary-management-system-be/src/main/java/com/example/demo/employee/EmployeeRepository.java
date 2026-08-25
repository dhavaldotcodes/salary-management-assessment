package com.example.demo.employee;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);

    Optional<Employee> findTopByOrderByIdDesc();

    @Query("select distinct e.country from Employee e order by e.country")
    List<String> findDistinctCountries();

    @Query("select distinct e.department from Employee e order by e.department")
    List<String> findDistinctDepartments();

    @Query("select distinct e.jobLevel from Employee e order by e.jobLevel")
    List<String> findDistinctJobLevels();
}
