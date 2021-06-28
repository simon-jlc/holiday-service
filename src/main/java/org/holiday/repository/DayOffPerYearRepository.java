package org.holiday.repository;

import org.holiday.domain.DayOffPerYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DayOffPerYearRepository extends JpaRepository<DayOffPerYear, Long> {

    Optional<DayOffPerYear> findByYear(int year);

}
