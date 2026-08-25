package com.example.demo.config;

import com.example.demo.domain.FxConverter;
import com.example.demo.employee.Employee;
import com.example.demo.employee.EmployeeRepository;
import com.example.demo.employee.EmploymentStatus;
import com.example.demo.fx.FxRate;
import com.example.demo.fx.FxRateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class DataSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);
    private static final int TARGET = 10_000;
    private static final LocalDate FX_AS_OF = LocalDate.of(2026, 1, 1);

    private static final String[] FIRST_NAMES = {
            "Aisha", "Alex", "Amelia", "Ananya", "Andre", "Amina", "Ben", "Camila", "Chen", "Diego",
            "Elena", "Fatima", "Grace", "Hiro", "Ibrahim", "James", "Julia", "Kenji", "Lina", "Marco",
            "Maya", "Nina", "Omar", "Priya", "Ravi", "Sofia", "Thomas", "Yuki", "Zoe", "Noah",
            "Leila", "Mateo", "Sara", "Wei", "Hannah", "Lucas", "Ines", "Arjun", "Nora", "Owen"
    };
    private static final String[] LAST_NAMES = {
            "Patel", "Nguyen", "Silva", "Garcia", "Kim", "Muller", "Singh", "Tanaka", "Costa", "Brown",
            "Khan", "Martin", "Santos", "Ali", "Johansson", "Rossi", "Novak", "Okafor", "Walsh", "Chen",
            "Dubois", "Kowalski", "Andersen", "Lopez", "Ito", "Berg", "Nair", "Fischer", "Hassan", "Murray"
    };
    private static final String[] DEPARTMENTS = {
            "Engineering", "Product", "Sales", "Marketing", "Finance", "People", "Operations",
            "Customer Support", "Legal", "Design"
    };
    private static final String[] LEVELS = {"L1", "L2", "L3", "L4", "L5", "L6"};

    private static final Map<String, String> COUNTRY_CURRENCY = Map.ofEntries(
            Map.entry("US", "USD"),
            Map.entry("IN", "INR"),
            Map.entry("GB", "GBP"),
            Map.entry("DE", "EUR"),
            Map.entry("FR", "EUR"),
            Map.entry("NL", "EUR"),
            Map.entry("IE", "EUR"),
            Map.entry("SG", "SGD"),
            Map.entry("AU", "AUD"),
            Map.entry("CA", "CAD"),
            Map.entry("JP", "JPY"),
            Map.entry("BR", "BRL"),
            Map.entry("CH", "CHF")
    );

    private static final String[] COUNTRY_WEIGHTS = {
            "US", "US", "US", "IN", "IN", "GB", "DE", "FR", "SG", "AU", "CA", "JP", "BR", "NL", "IE", "CH"
    };

    private final EmployeeRepository employeeRepository;
    private final FxRateRepository fxRateRepository;
    private final EntityManager entityManager;

    public DataSeeder(
            EmployeeRepository employeeRepository,
            FxRateRepository fxRateRepository,
            EntityManager entityManager
    ) {
        this.employeeRepository = employeeRepository;
        this.fxRateRepository = fxRateRepository;
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedFxRates();
        long existing = employeeRepository.count();
        if (existing >= TARGET) {
            log.info("Seed skipped — {} employees already present", existing);
            return;
        }
        log.info("Seeding {} employees (this can take a few seconds)...", TARGET);
        FxConverter fx = new FxConverter(fxMap());
        Random random = new Random(42);
        List<Employee> batch = new ArrayList<>(500);
        for (int i = 1; i <= TARGET; i++) {
            batch.add(buildEmployee(i, random, fx));
            if (batch.size() == 500) {
                employeeRepository.saveAll(batch);
                entityManager.flush();
                entityManager.clear();
                batch.clear();
            }
        }
        if (!batch.isEmpty()) {
            employeeRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
        }
        log.info("Seed complete: {} employees", employeeRepository.count());
    }

    private void seedFxRates() {
        List<FxRate> rates = List.of(
                rate("USD", "1.00000000"),
                rate("EUR", "1.08000000"),
                rate("GBP", "1.27000000"),
                rate("INR", "0.01200000"),
                rate("SGD", "0.74000000"),
                rate("AUD", "0.66000000"),
                rate("CAD", "0.73000000"),
                rate("JPY", "0.00670000"),
                rate("BRL", "0.18000000"),
                rate("CHF", "1.12000000")
        );
        fxRateRepository.saveAll(rates);
    }

    private static FxRate rate(String currency, String usd) {
        return new FxRate(currency, new BigDecimal(usd), FX_AS_OF);
    }

    private Map<String, BigDecimal> fxMap() {
        Map<String, BigDecimal> map = new LinkedHashMap<>();
        fxRateRepository.findAll().forEach(rate -> map.put(rate.getCurrency(), rate.getUsdRate()));
        return map;
    }

    private Employee buildEmployee(int index, Random random, FxConverter fx) {
        String country = COUNTRY_WEIGHTS[random.nextInt(COUNTRY_WEIGHTS.length)];
        String currency = COUNTRY_CURRENCY.get(country);
        String first = FIRST_NAMES[random.nextInt(FIRST_NAMES.length)];
        String last = LAST_NAMES[random.nextInt(LAST_NAMES.length)];
        String level = weightedLevel(random);
        String department = weightedDepartment(random);

        BigDecimal usd = usdCompensation(level, country, random);
        BigDecimal local = fx.fromUsd(usd, currency);
        BigDecimal bonus = BigDecimal.ZERO;
        if (random.nextDouble() < 0.45) {
            bonus = fx.fromUsd(usd.multiply(BigDecimal.valueOf(0.04 + random.nextDouble() * 0.10)), currency);
        }

        Employee employee = new Employee();
        employee.setEmployeeCode("ACME-%05d".formatted(index));
        employee.setFirstName(first);
        employee.setLastName(last);
        employee.setEmail("%s.%s.%d@acme.example".formatted(first.toLowerCase(), last.toLowerCase(), index));
        employee.setCountry(country);
        employee.setDepartment(department);
        employee.setJobLevel(level);
        employee.setStatus(random.nextDouble() < 0.04 ? EmploymentStatus.INACTIVE : EmploymentStatus.ACTIVE);
        employee.setBaseSalary(local);
        employee.setCurrency(currency);
        employee.setBonus(bonus);
        employee.setEffectiveDate(LocalDate.of(2023, 1, 1).plusDays(random.nextInt(900)));
        return employee;
    }

    private static String weightedLevel(Random random) {
        int roll = random.nextInt(100);
        if (roll < 18) {
            return "L1";
        }
        if (roll < 40) {
            return "L2";
        }
        if (roll < 65) {
            return "L3";
        }
        if (roll < 85) {
            return "L4";
        }
        if (roll < 95) {
            return "L5";
        }
        return "L6";
    }

    private static String weightedDepartment(Random random) {
        int roll = random.nextInt(100);
        if (roll < 32) {
            return "Engineering";
        }
        if (roll < 40) {
            return "Product";
        }
        if (roll < 54) {
            return "Sales";
        }
        if (roll < 62) {
            return "Marketing";
        }
        if (roll < 70) {
            return "Finance";
        }
        if (roll < 76) {
            return "People";
        }
        if (roll < 84) {
            return "Operations";
        }
        if (roll < 92) {
            return "Customer Support";
        }
        if (roll < 96) {
            return "Legal";
        }
        return "Design";
    }

    private static BigDecimal usdCompensation(String level, String country, Random random) {
        int[] band = switch (level) {
            case "L1" -> new int[] {42_000, 62_000};
            case "L2" -> new int[] {62_000, 88_000};
            case "L3" -> new int[] {88_000, 125_000};
            case "L4" -> new int[] {125_000, 170_000};
            case "L5" -> new int[] {170_000, 230_000};
            default -> new int[] {230_000, 320_000};
        };
        double location = switch (country) {
            case "IN" -> 0.32;
            case "BR" -> 0.42;
            case "JP" -> 0.82;
            case "US" -> 1.00;
            case "CH" -> 1.12;
            case "GB" -> 0.94;
            case "SG" -> 0.90;
            case "IE" -> 0.96;
            case "DE", "NL" -> 0.88;
            case "FR" -> 0.86;
            case "AU", "CA" -> 0.84;
            default -> 0.90;
        };
        double spread = band[0] + random.nextDouble() * (band[1] - band[0]);
        return BigDecimal.valueOf(spread * location);
    }
}
