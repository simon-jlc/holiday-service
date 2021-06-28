package org.holiday.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DayOffPerYearNotInitializedException extends RuntimeException {

    private final int year;

    @Override
    public String getMessage() {
        return String.format("Year < %s > not initialized in data table (aka: th_day_off_per_year)", year);
    }
}
