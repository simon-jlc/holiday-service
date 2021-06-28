package org.holiday.exception;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class NoDayOffAvailableException extends RuntimeException {

    private String employeeEmail;
    private Integer year;

    @Override
    public String getMessage() {
        return String.format("Employee (%s) does not have days off available for %d", employeeEmail, year);
    }
}
