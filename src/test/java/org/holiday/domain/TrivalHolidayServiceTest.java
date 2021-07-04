package org.holiday.domain;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.holiday.TrivalHolidayApplicationTests;
import org.holiday.api.vm.HolidaySearchCriteriaVM;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TrivalHolidayServiceTest extends TrivalHolidayApplicationTests {

    @Autowired
    private TrivalHolidayService sut;

    private String clarettaEthridgeEmail, winifredRunningGoatEmail;

    @BeforeAll
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

    @AfterAll
    void cleanUp() {
        dayOffRepo.deleteAllInBatch();
        empDayOffBalanceRepository.deleteAllInBatch();
        employeeRepository.deleteAllInBatch();
    }

    @Test
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

        // cleanup
        sut.removeDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-21"));
    }

    @Test
    void should_remove_a_day_off_to_employee() {
        // add two days off
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-21"));
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-22"));
        var clarettaEthridge = employeeRepository.findByEmail("c.ehtridge@aol.us").orElseThrow();
        assertThat(clarettaEthridge.getDaysOff()).hasSize(2);

        // remove one
        sut.removeDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-22"));
        clarettaEthridge = employeeRepository.findByEmail("c.ehtridge@aol.us").orElseThrow();
        assertThat(clarettaEthridge.getDaysOff()).hasSize(1);

        // cleanup
        sut.removeDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-21"));
    }

    @Test
    @Transactional
    void should_find_all_when_no_filter_apply() {
        createDaysOffDataSet();

        // when no filter are applied
        var searchCriteria = HolidaySearchCriteriaVM.builder().build();
        assertThat(sut.findDayOfByCriteria(searchCriteria)).hasSize(5);
    }

    @Test
    @Transactional
    void should_find_all_using_email_filter() {
        createDaysOffDataSet();

        // when filter on one employee
        var emails = Sets.newHashSet(clarettaEthridgeEmail);
        var searchCriteria = HolidaySearchCriteriaVM.builder().email(emails).build();
        assertThat(sut.findDayOfByCriteria(searchCriteria)).hasSize(3);

        // when filter on a set of employee's email
        emails = Sets.newHashSet(clarettaEthridgeEmail, winifredRunningGoatEmail);
        searchCriteria = HolidaySearchCriteriaVM.builder().email(emails).build();
        assertThat(sut.findDayOfByCriteria(searchCriteria)).hasSize(5);
    }

    @Test
    @Transactional
    void should_find_by_year() {
        createDaysOffDataSet();

        var searchCriteria = HolidaySearchCriteriaVM.builder().year(2019).build();
        var employees = sut.findDayOfByCriteria(searchCriteria);
        assertThat(employees).hasSize(1);
    }

    private void createDaysOffDataSet() {
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2020-12-31"));
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-01"));
        sut.addDayOff(clarettaEthridgeEmail, LocalDate.parse("2021-06-02"));

        sut.addDayOff(winifredRunningGoatEmail, LocalDate.parse("2019-12-31"));
        sut.addDayOff(winifredRunningGoatEmail, LocalDate.parse("2020-06-22"));
    }
}
