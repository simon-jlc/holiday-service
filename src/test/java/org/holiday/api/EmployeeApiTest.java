package org.holiday.api;

import org.holiday.StdTestConfig;
import org.holiday.TrivalHolidayApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@WebMvcTest(UserApi.class)
//@Import(StdTestConfig.class)
@ActiveProfiles("test")
class EmployeeApiTest {

//    @Autowired
//    private MockMvc mockMvc;
//
//    @Test
//    public void should_create_new_user() throws Exception {
//        var payload = """
//                {}
//                """;
//
//        this.mockMvc.perform(
//                post("/api/users")
//                        .accept(MediaType.APPLICATION_JSON_VALUE)
//                        .contentType(MediaType.APPLICATION_JSON_VALUE)
//                        .content(payload))
//                .andExpect(status().isOk());
//
//    }

}
