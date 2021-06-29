package org.holiday.config.boostrap;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.Set;

import static org.holiday.security.Authority.DEVELOPER;

@Slf4j
@Service
@Profile("withSmallDataset")
public class SmallDatasetInitializer extends AbstractDatasetInitializer {

    @PostConstruct
    public void initialize() {
        if (employeeRepository.count() > 0) {
            log.info("Database is already initialized. Truncate all table to force a new initialization.");
            return;
        }
        super.initialize();

        var developer = authorityRepository.findByName(DEVELOPER);

        // data set
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        var firstNameSet = Set.of("Claretta", "Winifred");
        var lastNameSet = Set.of("Ethridge", "Running Goat");

        // bulk save all employees
        var employees = bulkCreate(employeePwd, developer, firstNameSet, lastNameSet);
        employeeRepository.saveAll(employees);

        var employeesWithDaysOff = bulkAddDayOff(employees, 2018, 2020);
        employeeRepository.saveAll(employeesWithDaysOff);
    }
}
