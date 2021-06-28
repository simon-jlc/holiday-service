package org.holiday.repository;

import org.holiday.domain.DayOffPerYear;
import org.holiday.domain.Employee;
import org.holiday.domain.EmployeeDayOffBalance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeDayOffBalanceRepository extends JpaRepository<EmployeeDayOffBalance, Long> {

    Optional<EmployeeDayOffBalance> findByEmployeeAndYear(final Employee employee, final int year);

}
