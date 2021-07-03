package org.holiday.domain;

import lombok.Data;
import org.holiday.exception.NoDayOffAvailableException;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import static com.google.common.base.Preconditions.checkNotNull;

@Entity
@Table(name = "th_emp_day_off_balance")
@Data
public class EmployeeDayOffBalance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private Employee employee;

    @Min(value = 1900)
    @Max(value = 2099)
    @Column(nullable = false)
    private Integer year;

    private Integer balance;

    public void decrementBalance() {
        if (hasReachedLimit()) {
            throw new NoDayOffAvailableException(employee.getEmail(), year);
        }
        balance--;
    }

    public boolean hasReachedLimit() {
        checkNotNull(balance);
        return balance == 0;
    }
}
