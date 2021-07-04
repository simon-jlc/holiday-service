package org.holiday;

import org.holiday.domain.TrivalHolidayService;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("test")
@TestConfiguration
public class WithMockHolidayServiceConfig extends StdTestConfig{

    @Bean
    @Primary
    public TrivalHolidayService mockTrivalHolidayService() {
        return Mockito.mock(TrivalHolidayService.class);
    }
}
