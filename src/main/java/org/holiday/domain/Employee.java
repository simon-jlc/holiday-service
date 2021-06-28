package org.holiday.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.NaturalId;
import org.holiday.security.Authority;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "th_employee")
@Data
@ToString(exclude = { "daysOff", "authorities" })
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Email
    @Column(unique = true, nullable = false)
    @NaturalId
    private String email;

    @JsonIgnore
    @NotNull
    @Size(min = 60, max = 60)
    @Column(length = 60, nullable = false)
    private String password;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String firstName;

    @NotBlank
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String lastName;

    @ManyToMany(cascade = { CascadeType.REFRESH, CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "th_employee_dayoff", joinColumns = @JoinColumn(name = "employee_id"), inverseJoinColumns = @JoinColumn(name = "dayoff_id"))
    private Set<DayOff> daysOff = new HashSet<>();

    @JsonIgnore
    @ManyToMany
    @JoinTable(name = "th_employee_authority", joinColumns = { @JoinColumn(name = "employee_id", referencedColumnName = "id") }, inverseJoinColumns = { @JoinColumn(name = "authority_name", referencedColumnName = "name") })
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @BatchSize(size = 20)
    private Set<Authority> authorities;

    public boolean addDayOff(@NotNull DayOff dayOff) {
        dayOff.getEmployees().add(this);
        return daysOff.add(dayOff);
    }

    public boolean removeDayOff(@NotNull DayOff dayOff) {
        dayOff.getEmployees().remove(this);
        return daysOff.remove(dayOff);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        final Employee employee = (Employee) o;
        return Objects.equals(email, employee.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }
}
