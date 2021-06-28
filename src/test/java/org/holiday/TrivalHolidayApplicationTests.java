package org.holiday;

import org.holiday.domain.DayOffPerYear;
import org.holiday.domain.Employee;
import org.holiday.domain.TrivalHolidayService;
import org.holiday.repository.DayOffPerYearRepository;
import org.holiday.repository.DayOffRepository;
import org.holiday.repository.EmployeeDayOffBalanceRepository;
import org.holiday.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import javax.annotation.PostConstruct;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
@ActiveProfiles("test")
public class TrivalHolidayApplicationTests {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Autowired
    protected EmployeeRepository employeeRepository;

    @Autowired
    protected EmployeeDayOffBalanceRepository empDayOffBalanceRepository;

    @Autowired
    protected DayOffRepository dayOffRepo;

    @Autowired
    protected DayOffPerYearRepository calendarRepo;

    protected DayOffPerYear calendar2021;

    @PostConstruct
    public void doInitCalendar() {
        if (calendarRepo.count() != 0) {
            return;
        }

        var dayOffPerYearStream = IntStream.range(2019, 2029).mapToObj(year -> {
            var dayOffPerYear = new DayOffPerYear();
            dayOffPerYear.setYear(year);
            dayOffPerYear.setDaysOffCount(25);
            return dayOffPerYear;
        }).collect(Collectors.toSet());
        calendarRepo.saveAll(dayOffPerYearStream);
    }

    public Employee newEmployee() {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Claretta");
        employee.setLastName("Ethridge");
        employee.setEmail("c.ehtridge@aol.us");
        employee.setPassword(employeePwd);
        return employee;
    }

    protected Employee anotherNewEmployee() {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Winifred");
        employee.setLastName("Running Goat");
        employee.setEmail("w.runninggoat@aol.us");
        employee.setPassword(employeePwd);
        return employee;
    }
}
