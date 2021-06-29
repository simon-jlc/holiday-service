package org.holiday.config;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.holiday.domain.DayOffPerYear;
import org.holiday.domain.Employee;
import org.holiday.domain.TrivalHolidayService;
import org.holiday.repository.AuthorityRepository;
import org.holiday.repository.DayOffPerYearRepository;
import org.holiday.repository.EmployeeRepository;
import org.holiday.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Creation date: 29/06/2021
 *
 */
@Component
@Profile({ "!test", "withLargeDataset" })
@Slf4j
public class LargeDatasetInitializer {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private DayOffPerYearRepository calendarRepo;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private TrivalHolidayService holidayService;

    @PostConstruct
    public void initialize() {
        if (employeeRepository.count() > 0) {
            log.info("Database is already initialized. Truncate all table to force a new initialization.");
            return;
        }

        // initialize all calendars with 25 days off by default per year
        var dayOffPerYearStream = IntStream.range(1990, 2099).mapToObj(year -> {

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


        var employeePwd = passwordEncoder.encode("HelloWorld!");
        var firstNameSet = Set.of("Claretta", "Winifred", "Carl", "Joe", "Lou", "Tahiti", "Bart", "Kent", "Julius", "Maude");
        var lastNameSet = Set.of("Ethridge", "Running Goat", "Simpson", "Taupeman", "Smithers", "Quimby", "Carlson", "Bob", "Mel");

        final Random rn = new Random();
        Set<Employee> employees = new HashSet<>();
        // 10 * 10 => 100 users
        for (String firstName : firstNameSet) {
            for (String lastName : lastNameSet) {
                var firstNameFirstLetter = firstName.substring(0, 1).toLowerCase();
                var lowerCaseLastName = transformLastName(lastName);
                var email = firstNameFirstLetter + "." + lowerCaseLastName + "@aol.us";

                var employee = new Employee();
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setEmail(email);
                employee.setPassword(employeePwd);
                employee.setAuthorities(Set.of(developer));
                employees.add(employee);
            }
        }

        // bulk save all employees, with days off
        employeeRepository.saveAll(employees);

        for (Employee employee : employees) {
            IntStream.range(1990, 2020).forEach(year -> {
                int totalDayToAdd = rn.nextInt(20);
                for (int i = 0; i < totalDayToAdd - 1; i++) {
                    // then generate a random date
                    long startDay = LocalDate.of(year, Month.JANUARY, 1).toEpochDay();
                    long endDay = LocalDate.of(year, Month.DECEMBER, 31).toEpochDay();
                    long dayInRange = ThreadLocalRandom.current().nextLong(startDay, endDay);
                    var dayOffDate = LocalDate.ofEpochDay(dayInRange);
                    holidayService.addDayOff(employee.getEmail(), dayOffDate);
                }
            });
        }

        employeeRepository.saveAll(employees);
    }

    private String transformLastName(String lastName) {
        var allComposeName = StringUtils.tokenizeToStringArray(lastName, " ");
        return Arrays.stream(allComposeName).map(String::toLowerCase).collect(Collectors.joining());
    }
}
