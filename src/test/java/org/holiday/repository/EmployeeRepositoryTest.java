package org.holiday.repository;

import org.holiday.TrivalHolidayApplicationTests;
import org.holiday.domain.DayOff;
import org.holiday.domain.Employee;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class EmployeeRepositoryTest extends TrivalHolidayApplicationTests {

    @AfterEach
    public void cleanModel() {
        employeeRepository.deleteAll();
    }

    @Test
    public void should_validate_and_create_new_employee() {
        Employee employee = newEmployee();
        var employeeSaved = employeeRepository.save(employee);
        assertThat(employeeSaved).isNotNull();
    }

    @Test
    public void should_validate_and_create_new_employee_2() {
        Employee employee_1 = newEmployee();
        employeeRepository.save(employee_1);

        Employee employee_2 = anotherNewEmployee();
        var employeeSaved = employeeRepository.save(employee_2);
        assertThat(employeeSaved).isNotNull();
    }

    @Test
    @Transactional
    public void should_save_employee_days_off() {
        var dayOff = new DayOff();
        dayOff.setDayOff(LocalDate.parse("2021-06-21"));
        dayOffRepo.save(dayOff);

        var employee = newEmployee();
        employeeRepository.save(employee);

        employee.addDayOff(dayOff);
        employeeRepository.save(employee);

        var persistedEmployee = employeeRepository.findByEmail(employee.getEmail()).orElseThrow();
        assertThat(persistedEmployee.getDaysOff()).hasSize(1);
    }

    @Test
    @Transactional
    public void should_remove_employee_days_off() {
        var date = LocalDate.parse("2021-06-21");
        var dayOff = new DayOff();
        dayOff.setDayOff(date);
        dayOffRepo.save(dayOff);
        var employee = newEmployee();
        employeeRepository.save(employee);
        employee.addDayOff(dayOff);
        employeeRepository.save(employee);

        assertThat(employee.removeDayOff(dayOff)).isTrue();

        var persistedEmployee = employeeRepository.findByEmail(employee.getEmail()).orElseThrow();
        assertThat(persistedEmployee.getDaysOff()).hasSize(0);

        var byDayOff = dayOffRepo.findByDayOff(date).orElseThrow();
        assertThat(byDayOff.getEmployees()).isEmpty();
    }

    @Test
    public void should_not_save_employee_with_same_email() {
        employeeRepository.save(newEmployee());
        assertThatCode(() -> employeeRepository.save(newEmployee())).hasMessageContaining("could not execute statement");
    }

    @Test
    public void should_not_save_invalid_employee() {
        final var employee = new Employee();
        assertThatCode(() -> employeeRepository.save(employee)).hasMessageContaining("Validation failed");
    }
}
