package org.holiday.api;

import org.assertj.core.util.Lists;
import org.holiday.TrivalHolidayApplicationTests;
import org.holiday.WithMockHolidayServiceConfig;
import org.holiday.api.vm.HolidaySearchCriteriaVM;
import org.holiday.domain.EmployeeDayOffDto;
import org.holiday.domain.TrivalHolidayService;
import org.holiday.security.Authority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@Import(WithMockHolidayServiceConfig.class)
class TrivalHolidayControllerTest extends TrivalHolidayApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TrivalHolidayService mockHolidayService;

    @Test
    public void should_controller_be_secured() throws Exception {
        this.mockMvc.perform(
                get("/api/holiday")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user1", password = "pwd", roles = Authority.DEVELOPER)
    public void should_find_all_employees() throws Exception {
        // prepare
        var first = new EmployeeDayOffDto(1L, "c.ehtridge@aol.us", "A", "C", LocalDate.parse("2021-01-02"));
        var second = new EmployeeDayOffDto(2L, "w.runninggoat@aol.us", "B", "D", LocalDate.parse("2021-01-02"));
        var noFilterSearch = HolidaySearchCriteriaVM.defaultSearch();
        when(mockHolidayService.findDayOfByCriteria(noFilterSearch)).thenReturn(Lists.newArrayList(first, second));

        // when
        this.mockMvc.perform(
                get("/api/holiday")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpect(content().json("" +
                    "[\n" +
                    "  {\n" +
                    "    \"employee_id\": 1,\n" +
                    "    \"email\": \"c.ehtridge@aol.us\",\n" +
                    "    \"first_name\": \"A\",\n" +
                    "    \"last_name\": \"C\",\n" +
                    "    \"day_off\": \"2021-01-02\"\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"employee_id\": 2,\n" +
                    "    \"email\": \"w.runninggoat@aol.us\",\n" +
                    "    \"first_name\": \"B\",\n" +
                    "    \"last_name\": \"D\",\n" +
                    "    \"day_off\": \"2021-01-02\"\n" +
                    "  }\n" +
                    "]"
        ));
    }
}
