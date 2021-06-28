package org.holiday.batch;

import java.util.Date;

/**
 * A row of employee's day off CSV extracted by batch
 */
public record EmployeeDayOffRecord(
        Long id,
        String email,
        String firstName,
        String lastName,
        Date dayOff,
        Integer year,
        Integer yearAvailableDayOff,
        Integer daysOffTaken,
        Integer yearBalance
) {

    /**
     * All record fields used by introspection.
     * Also used to compute CSV header.
     */
    public static final String[] RECORD_FIELDS = {"id", "email", "firstName", "lastName", "dayOff", "year", "yearAvailableDayOff", "daysOffTaken", "yearBalance"};

}
