package org.holiday.domain;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.holiday.api.vm.HolidaySearchCriteriaVM;
import org.holiday.exception.DayOffPerYearNotInitializedException;
import org.holiday.exception.EmployeeNotFoundException;
import org.holiday.repository.DayOffPerYearRepository;
import org.holiday.repository.DayOffRepository;
import org.holiday.repository.EmployeeDayOffBalanceRepository;
import org.holiday.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrivalHolidayService {

    @Autowired
    private DayOffRepository dayOffRepository;

    @Autowired
    private EmployeeRepository empRepository;

    @Autowired
    private EmployeeDayOffBalanceRepository empDayOffBalanceRepository;

    @Autowired
    private DayOffPerYearRepository dayOffPerYearRepository;

    @Autowired
    private EntityManager em;

    /**
     * A search day off service to find all employee's days off by criteria.
     * It can filter on:
     * - employee's email array
     * - specified year days off filter
     * - from, to if needed
     * <p>
     * In case of search criteria are empty, then a global search is made.
     * <p>
     * <p>
     *
     * @param search
     * @return
     */
    public List<EmployeeDayOffDto> findDayOfByCriteria(@NotNull final HolidaySearchCriteriaVM search) {
        // get all stuff to create a dynamic Criteria query using JPA API
        Session session = em.unwrap(Session.class);
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<EmployeeDayOffDto> criteria = cb.createQuery(EmployeeDayOffDto.class);
        Root<Employee> employee = criteria.from(Employee.class);
        Join<Employee, DayOff> joinDaysOff = employee.join("daysOff");

        // get all paths
        Path<Long> idPath = employee.get("id");
        Path<String> emailPath = employee.get("email");
        Path<String> firstNamePath = employee.get("firstName");
        Path<String> lastNamePath = employee.get("lastName");
        Path<LocalDate> dayOffPath = joinDaysOff.get("dayOff");

        // build SQL predicates based on search
        List<Predicate> predicates = new ArrayList<>();
        if (!CollectionUtils.isEmpty(search.getEmail())) {
            predicates.add(employee.get("email").in(search.getEmail()));
        }
        if (null != search.getYear()) {
            predicates.add(cb.between(dayOffPath, search.fromDate(), search.toDate()));
        }

        // everything is ready, run the query and use DTO as projection
        return em.createQuery(
                criteria
                    .select( cb.construct( EmployeeDayOffDto.class, idPath, emailPath, firstNamePath, lastNamePath, dayOffPath ) )
                    .where( cb.and(predicates.toArray(Predicate[]::new)))
                    .orderBy( cb.asc(lastNamePath), cb.asc(firstNamePath), cb.asc(dayOffPath) )
        ).getResultList();
    }

    /**
     * Add a day off to an employee via its email.
     *
     * @param employeeEmail
     * @param dayOff
     */
    @Transactional
    public void addDayOff(final String employeeEmail, final LocalDate dayOff) {
        var employee = empRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));

        var persistDayOff = dayOffRepository.findByDayOff(dayOff).orElse(new DayOff(dayOff));

        // fetch needed dependencies, this table is in EAGER initialization at start time
        var yearAsked = persistDayOff.getDayOff().getYear();
        var dayOffPerYearAsked = dayOffPerYearRepository.findByYear(yearAsked)
                .orElseThrow(() -> new DayOffPerYearNotInitializedException(yearAsked));

        // a same day of added several time still works
        if (!employee.addDayOff(persistDayOff)) {
            log.info("{} day off was already taken by {}", dayOff, employee);
            return;
        }

        // finally decrement balance with an "upsert" like
        var employeeBalanceOfYearAsked = empDayOffBalanceRepository.findByEmployeeAndYear(employee, yearAsked)
                .orElseGet(() -> createBalance(employee, yearAsked, dayOffPerYearAsked.getDaysOffCount()));
        var newBalance = employeeBalanceOfYearAsked.decrementBalance();
        log.info("{} day off added {}. New balance is: {}", dayOff, employee, newBalance);

        empDayOffBalanceRepository.save(employeeBalanceOfYearAsked);
        empRepository.save(employee);
    }

    /**
     * Replace an employee day off by another one via employee's email.
     * <p>
     *
     * @param employeeEmail
     * @param prevDayOff
     * @param newDayOff
     */
    @Transactional
    public void replaceDayOffBy(final String employeeEmail, final LocalDate prevDayOff, final LocalDate newDayOff) {
        removeDayOff(employeeEmail, prevDayOff);
        addDayOff(employeeEmail, newDayOff);
    }

    /**
     * Remove a day off to an employee via its email.
     *
     * @param employeeEmail
     * @param dayOff
     */
    @Transactional
    public void removeDayOff(final String employeeEmail, final LocalDate dayOff) {
        var employee = empRepository.findByEmail(employeeEmail)
                .orElseThrow(() -> new EmployeeNotFoundException(employeeEmail));
        var dayOffRemove = dayOffRepository.findByDayOff(dayOff).orElseThrow(IllegalStateException::new);

        if (!employee.removeDayOff(dayOffRemove)) {
            log.info("{} day off was already removed by {}", dayOff, employee);
            return;
        }

        var yearAsked = dayOffRemove.getDayOff().getYear();
        // finally increment balance
        var employeeBalanceOfYearAsked = empDayOffBalanceRepository.findByEmployeeAndYear(employee, yearAsked).orElseThrow();
        var newBalance = employeeBalanceOfYearAsked.incrementBalance();
        log.info("{} day off removed {}. New balance is: {}", dayOff, employee, newBalance);

        empDayOffBalanceRepository.save(employeeBalanceOfYearAsked);
        empRepository.save(employee);
    }

    /**
     * When employee's balance is not yet initialized, then create an empty one
     *
     * @param employee  : current employee
     * @param yearAsked : current year asked by user
     * @param balance   : total days off per this year, got from calendar info
     * @return
     */
    private EmployeeDayOffBalance createBalance(final Employee employee, final Integer yearAsked, final Integer balance) {
        log.debug("a new vacation balance is created for {}", employee);
        var employeeDayOffBalance = new EmployeeDayOffBalance();
        employeeDayOffBalance.setEmployee(employee);
        employeeDayOffBalance.setYear(yearAsked);
        employeeDayOffBalance.setBalance(balance);
        return empDayOffBalanceRepository.save(employeeDayOffBalance);
    }
}
