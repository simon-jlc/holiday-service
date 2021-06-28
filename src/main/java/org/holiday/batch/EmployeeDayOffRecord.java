package org.holiday.batch;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.util.Date;

/**
 * A row of employee's day off CSV extracted by batch
 */
@Value
@AllArgsConstructor
public class EmployeeDayOffRecord {

    /**
     * All record fields used by introspection.
     * Also used to compute CSV header.
     */
    public static final String[] RECORD_FIELDS = {"id", "email", "firstName", "lastName", "dayOff", "year", "yearAvailableDayOff", "daysOffTaken", "yearBalance"};

    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private Date dayOff;
    private Integer year;
    private Integer yearAvailableDayOff;
    private Integer daysOffTaken;
    private Integer yearBalance;

}
