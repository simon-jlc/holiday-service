package org.holiday.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.holiday.domain.DayOffPerYear;
import org.holiday.domain.Employee;
import org.holiday.repository.AuthorityRepository;
import org.holiday.repository.DayOffPerYearRepository;
import org.holiday.repository.EmployeeRepository;
import org.holiday.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@Profile("!withLargeDataset")
public class DatabaseInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DayOffPerYearRepository calendarRepo;

    @Autowired
    private AuthorityRepository authorityRepository;

    @PostConstruct
    public void initialize() {
        if (employeeRepository.count() > 0) {
            log.info("Database is already initialized. Truncate all table to force a new initialization.");
            return;
        }

        // initialize all calendars with 25 days off by default per year
        var dayOffPerYearStream = IntStream.range(2000, 2099).mapToObj(year -> {

            // jsut to make some test on 2020
            var availableDayOff = year == 2020 ? 5 : 20;

            var dayOffPerYear = new DayOffPerYear();
            dayOffPerYear.setYear(year);
            dayOffPerYear.setDaysOffCount(availableDayOff);
            return dayOffPerYear;
        }).collect(Collectors.toSet());
        calendarRepo.saveAll(dayOffPerYearStream);

        // initialize security roles
        var developer = new Authority();
        developer.setName("DEVELOPER");
        var consultant = new Authority();
        consultant.setName("CONSULTANT");
        authorityRepository.saveAll(Lists.newArrayList(developer, consultant));

        // initialized two employees
        var aDeveloperEmployee = newEmployee(developer);
        var aConsultantEmployee = anotherNewEmployee(consultant);
        var anotherEmployee = anotherYetNewEmployee(consultant);
        var employees = Lists.newArrayList(aDeveloperEmployee, aConsultantEmployee, anotherEmployee);
        employeeRepository.saveAll(employees);
    }

    private Employee newEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Claretta");
        employee.setLastName("Ethridge");
        employee.setEmail("c.ehtridge@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

    private Employee anotherNewEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Winifred");
        employee.setLastName("Running Goat");
        employee.setEmail("w.runninggoat@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

    private Employee anotherYetNewEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Ned");
        employee.setLastName("Flanders");
        employee.setEmail("n.flanders@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

}
