package org.holiday.domain;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.holiday.TrivalHolidayApplicationTests;
import org.holiday.api.vm.HolidaySearchCriteriaVM;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestMethodOrder(OrderAnnotation.class)
class TrivalHolidayServiceTest extends TrivalHolidayApplicationTests {

    @Autowired
    private TrivalHolidayService sut;

    private String clarettaEthridgeEmail, winifredRunningGoatEmail;

    @BeforeEach
    void setup() {
        // keep emails
        clarettaEthridgeEmail = "c.ehtridge@aol.us";
        winifredRunningGoatEmail = "w.runninggoat@aol.us";

        // init if necessary
        employeeRepository.findByEmail("c.ehtridge@aol.us")
                .orElseGet(() -> employeeRepository.save(newEmployee()));

        employeeRepository.findByEmail("w.runninggoat@aol.us")
                .orElseGet(() -> employeeRepository.save(anotherNewEmployee()));
    }

    @Test
    @Order(1)
    void should_add_a_day_off_to_employee() {
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-21"));
        // then
        var clarettaEthridge = employeeRepository.findByEmail("c.ehtridge@aol.us").orElseThrow();
        var summerDay = Iterables.getFirst(clarettaEthridge.getDaysOff(), null);
        assertThat(summerDay).isNotNull().hasFieldOrPropertyWithValue("dayOff", LocalDate.parse("2021-06-21"));

        var balanceOfDayOff = empDayOffBalanceRepository.findByEmployeeAndYear(clarettaEthridge, 2021);
        assertThat(balanceOfDayOff).isPresent().get()
                .hasFieldOrPropertyWithValue("year", 2021)
                .hasFieldOrPropertyWithValue("balance", 24);
    }

    @Test
    @Order(2)
    void should_remove_a_day_off_to_employee() {
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-22"));
        var clarettaEthridge = employeeRepository.findByEmail("c.ehtridge@aol.us").orElseThrow();
        assertThat(clarettaEthridge.getDaysOff()).hasSize(2);

        sut.removeDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-22"));
        clarettaEthridge = employeeRepository.findByEmail("c.ehtridge@aol.us").orElseThrow();
        assertThat(clarettaEthridge.getDaysOff()).hasSize(1);
    }

    @Test
    @Order(3)
    void should_find_by_employee() {
        // prepare
        var _2021 = 2021;
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2020-12-31"));
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-01"));
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-02"));

        sut.addDayOff(winifredRunningGoatEmail, LocalDate.parse("2019-12-31"));
        sut.addDayOff(winifredRunningGoatEmail, LocalDate.parse("2020-06-22"));
        var emails = Sets.newHashSet(clarettaEthridgeEmail);
        var searchCriteria = HolidaySearchCriteriaVM.builder()
                .email(emails)
                .year(_2021).build();

        // when
        var foundEmployeesWithDaysOff = sut.findDayOfByCriteria(searchCriteria);

        // then
        assertThat(foundEmployeesWithDaysOff).hasSize(1);
        var foundEmployee = Iterables.getFirst(foundEmployeesWithDaysOff, null);
        assertThat(foundEmployee.getEmail()).isEqualTo(clarettaEthridgeEmail);
        assertThat(foundEmployee.getDaysOff()).hasSize(2);
    }
}
