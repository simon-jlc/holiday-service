package org.holiday.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "th_day_off")
@NoArgsConstructor
@Data
@ToString(exclude = "employees")
public class DayOff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @NaturalId
    private LocalDate dayOff;

    @ManyToMany(mappedBy = "daysOff")
    private Set<Employee> employees = new HashSet<>();

    public DayOff(final LocalDate dayOff) {
        this.dayOff = dayOff;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final DayOff dayOff1 = (DayOff) o;
        return Objects.equals(dayOff, dayOff1.dayOff);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dayOff);
    }
}
