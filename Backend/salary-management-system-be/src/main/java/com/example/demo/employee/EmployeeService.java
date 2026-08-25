package com.example.demo.employee;

import com.example.demo.domain.FxConverter;
import com.example.demo.domain.UnknownCurrencyException;
import com.example.demo.employee.dto.EmployeeRequest;
import com.example.demo.employee.dto.EmployeeResponse;
import com.example.demo.employee.dto.LookupResponse;
import com.example.demo.employee.dto.PageResponse;
import com.example.demo.fx.FxRateRepository;
import com.example.demo.fx.FxService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class EmployeeService {

    private static final int MAX_PAGE_SIZE = 100;

    private final EmployeeRepository employeeRepository;
    private final FxService fxService;
    private final FxRateRepository fxRateRepository;

    public EmployeeService(
            EmployeeRepository employeeRepository,
            FxService fxService,
            FxRateRepository fxRateRepository
    ) {
        this.employeeRepository = employeeRepository;
        this.fxService = fxService;
        this.fxRateRepository = fxRateRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<EmployeeResponse> search(
            EmployeeQuery query,
            int page,
            int size,
            String sortField,
            String direction
    ) {
        Pageable pageable = PageRequest.of(
                Math.max(page, 0),
                clampSize(size),
                Sort.by(parseDirection(direction), sanitizeSort(sortField))
        );
        FxConverter fx = fxService.converter();
        Page<EmployeeResponse> result = employeeRepository
                .findAll(EmployeeSpecifications.from(query), pageable)
                .map(employee -> EmployeeResponse.from(employee, fx));
        return PageResponse.from(result);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse get(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        return EmployeeResponse.from(employee, fxService.converter());
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        FxConverter fx = fxService.converter();
        validateCurrency(fx, request.currency());
        String email = normalizeEmail(request.email());
        if (employeeRepository.existsByEmailIgnoreCase(email)) {
            throw new DuplicateEmailException(email);
        }
        Employee employee = new Employee();
        apply(employee, request, email, true);
        employee.setEmployeeCode(nextEmployeeCode());
        Employee saved = employeeRepository.save(employee);
        return EmployeeResponse.from(saved, fx);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        FxConverter fx = fxService.converter();
        validateCurrency(fx, request.currency());
        String email = normalizeEmail(request.email());
        if (employeeRepository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
            throw new DuplicateEmailException(email);
        }
        apply(employee, request, email, false);
        return EmployeeResponse.from(employeeRepository.save(employee), fx);
    }

    @Transactional
    public EmployeeResponse deactivate(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
        employee.setStatus(EmploymentStatus.INACTIVE);
        return EmployeeResponse.from(employeeRepository.save(employee), fxService.converter());
    }

    @Transactional(readOnly = true)
    public LookupResponse lookups() {
        List<String> currencies = fxRateRepository.findAll().stream()
                .map(rate -> rate.getCurrency())
                .sorted()
                .toList();
        return new LookupResponse(
                employeeRepository.findDistinctCountries(),
                employeeRepository.findDistinctDepartments(),
                employeeRepository.findDistinctJobLevels(),
                currencies
        );
    }

    private void apply(Employee employee, EmployeeRequest request, String email, boolean creating) {
        employee.setFirstName(request.firstName().trim());
        employee.setLastName(request.lastName().trim());
        employee.setEmail(email);
        employee.setCountry(request.country().trim().toUpperCase(Locale.ROOT));
        employee.setDepartment(request.department().trim());
        employee.setJobLevel(request.jobLevel().trim().toUpperCase(Locale.ROOT));
        employee.setBaseSalary(request.baseSalary());
        employee.setCurrency(request.currency().trim().toUpperCase(Locale.ROOT));
        employee.setBonus(request.bonus() == null ? BigDecimal.ZERO : request.bonus());
        employee.setEffectiveDate(request.effectiveDate());
        if (creating) {
            employee.setStatus(request.status() == null ? EmploymentStatus.ACTIVE : request.status());
        } else if (request.status() != null) {
            employee.setStatus(request.status());
        }
    }

    private String nextEmployeeCode() {
        long next = employeeRepository.findTopByOrderByIdDesc()
                .map(Employee::getId)
                .orElse(0L) + 1;
        return "ACME-%05d".formatted(next);
    }

    private static void validateCurrency(FxConverter fx, String currency) {
        if (!fx.supports(currency)) {
            throw new UnknownCurrencyException(currency);
        }
    }

    private static String normalizeEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private static int clampSize(int size) {
        if (size < 1) {
            return 25;
        }
        return Math.min(size, MAX_PAGE_SIZE);
    }

    private static Sort.Direction parseDirection(String direction) {
        if (direction != null && direction.equalsIgnoreCase("desc")) {
            return Sort.Direction.DESC;
        }
        return Sort.Direction.ASC;
    }

    private static String sanitizeSort(String sortField) {
        return switch (sortField == null ? "" : sortField) {
            case "firstName", "email", "country", "department", "jobLevel", "baseSalary", "status" -> sortField;
            default -> "lastName";
        };
    }
}
