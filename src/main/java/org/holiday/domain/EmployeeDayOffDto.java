package org.holiday.domain;

import lombok.Value;

import java.time.LocalDate;
import java.util.List;

@Value
public class EmployeeDayOffDto {
    private Long employeeId;
    private String email;
    private String firstName;
    private String lastName;
    private List<LocalDate> daysOff;
}
