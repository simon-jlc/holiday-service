package org.holiday.domain;

import lombok.Value;

import java.time.LocalDate;

@Value
public class EmployeeDayOffDto {
    private Long employeeId;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate dayOff;
}
