package org.holiday.api.vm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDate;
import java.time.Month;
import java.util.Collections;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Use as GET method request parameters.
 * Normally, I would prefer to constraint it as a value object,
 * but for cleaner code reason, I let it as mutable object to make Spring magic works simply.
 */
@Data
@AllArgsConstructor
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = HolidaySearchCriteriaVM.Builder.class)
public class HolidaySearchCriteriaVM {

    @Email
    private Set<String> email;

    @PositiveOrZero
    private Integer year;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }

    public LocalDate fromDate() {
        checkNotNull(year);
        return LocalDate.of(year, Month.JANUARY, 1);
    }

    public LocalDate toDate() {
        checkNotNull(year);
        return LocalDate.of(year, Month.DECEMBER, 31);
    }

    public boolean hasCriteria() {
        return (email != null && !email.isEmpty()) || year != null;
    }

    /**
     * A noop search criteria uses for a global search
     *
     * @return
     */
    public static HolidaySearchCriteriaVM defaultSearch() {
        return new HolidaySearchCriteriaVM(
                Collections.emptySet(),
                null
        );
    }
}
