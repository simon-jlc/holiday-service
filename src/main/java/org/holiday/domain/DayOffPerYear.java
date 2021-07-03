package org.holiday.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@Entity
@Table(name = "th_day_off_per_year")
@Data
public class DayOffPerYear {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 1900)
    @Max(value = 2099)
    @Column(unique = true, nullable = false)
    private Integer year;

    @PositiveOrZero
    @Column(nullable = false)
    private Integer daysOffCount;
}
