package org.holiday.repository;

import org.holiday.domain.DayOff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DayOffRepository extends JpaRepository<DayOff, Long> {

    Optional<DayOff> findByDayOff(LocalDate dayOff);

}
