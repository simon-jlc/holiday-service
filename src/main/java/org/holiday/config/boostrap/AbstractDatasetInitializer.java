package org.holiday.config.boostrap;

import org.holiday.domain.DayOffPerYear;
import org.holiday.domain.Employee;
import org.holiday.domain.TrivalHolidayService;
import org.holiday.repository.AuthorityRepository;
import org.holiday.repository.DayOffPerYearRepository;
import org.holiday.repository.EmployeeRepository;
import org.holiday.security.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.Month;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AbstractDatasetInitializer {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected DayOffPerYearRepository calendarRepo;

    @Autowired
    protected AuthorityRepository authorityRepository;

    @Autowired
    protected TrivalHolidayService holidayService;

    public void initialize() {
        initializeCalendarInfos();
        initializeSecurityAuthorities();
    }

    /**
     * Generate a set of employees using combination of firstNameSet and lastNameSet.
     * At the end you'll get a set of n * m employees.
     *
     * @param passwordForAll
     * @param roleForAll
     * @param firstNameSet
     * @param lastNameSet
     * @return
     */
    protected Set<Employee> bulkCreate(final String passwordForAll, final Authority roleForAll, final Set<String> firstNameSet, final Set<String> lastNameSet) {
        var employees = new HashSet<Employee>();
        for (String firstName : firstNameSet) {
            for (String lastName : lastNameSet) {
                var firstNameFirstLetter = firstName.substring(0, 1).toLowerCase();
                var lowerCaseLastName = transformLastName(lastName);
                var email = firstNameFirstLetter + "." + lowerCaseLastName + "@aol.us";
                var encodePwd = passwordEncoder.encode("HelloWorld!");

                var employee = new Employee();
                employee.setFirstName(firstName);
                employee.setLastName(lastName);
                employee.setEmail(email);
                employee.setPassword(encodePwd);
                employee.setAuthorities(Set.of(roleForAll));
                employees.add(employee);
            }
        }
        return employees;
    }

    protected Set<Employee> bulkAddDayOff(final Set<Employee> employees, int startYear, int endYear) {
        // then compute a randomized dataset of day off
        // took on last 30 years for each of employees
        var rn = new Random();
        for (Employee employee : employees) {
            IntStream.range(startYear, endYear).forEach(year -> {
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
        return employees;
    }

    /**
     * initialize all calendars with 25 days off by default per year
     *  > 5 days off on 2020 to make some tests
     */
    private void initializeCalendarInfos() {

        var dayOffPerYearStream = IntStream.range(2000, 2099).mapToObj(year -> {

            // jsut to make some test on 2020
            var availableDayOff = year == 2020 ? 5 : 20;

            var dayOffPerYear = new DayOffPerYear();
            dayOffPerYear.setYear(year);
            dayOffPerYear.setDaysOffCount(availableDayOff);
            return dayOffPerYear;
        }).collect(Collectors.toSet());
        calendarRepo.saveAll(dayOffPerYearStream);
    }

    private void initializeSecurityAuthorities() {
        var roles = List.of("DEVELOPER", "CONSULTANT").stream()
            .map(this::createAuthority)
            .collect(Collectors.toList());

        authorityRepository.saveAll(roles);
    }

    private Authority createAuthority(String role) {
        var authority = new Authority();
        authority.setName(role);
        return authority;
    }

    private String transformLastName(String lastName) {
        var allComposeName = StringUtils.tokenizeToStringArray(lastName, " ");
        return Arrays.stream(allComposeName).map(String::toLowerCase).collect(Collectors.joining());
    }
}
