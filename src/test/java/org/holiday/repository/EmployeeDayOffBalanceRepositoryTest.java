package org.holiday.repository;

import org.holiday.TrivalHolidayApplicationTests;
import org.holiday.domain.EmployeeDayOffBalance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EmployeeDayOffBalanceRepositoryTest extends TrivalHolidayApplicationTests {

    @AfterEach
    public void cleanModel() {
        empDayOffBalanceRepository.deleteAll();
        employeeRepository.deleteAll();
    }

    @Test
    void should_add_day_off_balance_of_employee() {
        // prepare
        var employee = employeeRepository.save(newEmployee());
        var employeeDayOffBalance = new EmployeeDayOffBalance();
        employeeDayOffBalance.setYear(2021);
        employeeDayOffBalance.setEmployee(employee);
        employeeDayOffBalance.setBalance(20);
        empDayOffBalanceRepository.save(employeeDayOffBalance);

        // when & then
        assertThat(empDayOffBalanceRepository.findByEmployeeAndYear(employee, 2021)).isPresent().get()
                .hasFieldOrPropertyWithValue("year", 2021)
                .hasFieldOrPropertyWithValue("balance", 20)
                .hasFieldOrPropertyWithValue("employee.email", "c.ehtridge@aol.us")
                .hasFieldOrPropertyWithValue("employee.firstName", "Claretta")
                .hasFieldOrPropertyWithValue("employee.lastName", "Ethridge")
        ;
    }
}
