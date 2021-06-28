package org.holiday.api.vm;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import lombok.Builder;
import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Value
@Builder(builderClassName = "Builder")
@JsonDeserialize(builder = HolidayVM.Builder.class)
public class HolidayVM {

    @Email
    @NotNull
    private String employeeEmail;

    @NotNull
    private LocalDate dayOff;

    @JsonPOJOBuilder(withPrefix = "")
    public static class Builder {
    }
}
