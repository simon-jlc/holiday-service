package org.holiday.repository;

import org.holiday.domain.Employee;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e LEFT JOIN FETCH e.daysOff WHERE e.email = (:email)")
    Optional<Employee> findByEmail(@Param("email") String email);

    @EntityGraph(attributePaths = "authorities")
    @Cacheable(cacheNames = "employeesByEmail")
    Optional<Employee> findOneWithAuthoritiesByEmailIgnoreCase(String email);

}
