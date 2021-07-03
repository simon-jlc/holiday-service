package org.holiday.config.boostrap;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.holiday.domain.Employee;
import org.holiday.security.Authority;
import org.springframework.stereotype.Service;

import java.util.Set;

import static org.holiday.security.Authority.CONSULTANT;
import static org.holiday.security.Authority.DEVELOPER;

@Slf4j
@Service
public class EmployeeModelInitializer extends AbstractDatasetInitializer {

    public void initialize() {
        // initialized two employees
        var devAuth = authorityRepository.findByName(DEVELOPER);
        var consultantAuth = authorityRepository.findByName(CONSULTANT);

        var clarettaDev = clarettaEthridgeEmployee(devAuth);
        var winifredConsultant = winifredRunningGoatEmployee(consultantAuth);
        var anotherEmployee = nedFlanderEmployee(consultantAuth);

        // bulk save
        var employees = Lists.newArrayList(clarettaDev, winifredConsultant, anotherEmployee);
        employeeRepository.saveAll(employees);
    }

    private Employee clarettaEthridgeEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Claretta");
        employee.setLastName("Ethridge");
        employee.setEmail("c.ehtridge@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

    private Employee winifredRunningGoatEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Winifred");
        employee.setLastName("Running Goat");
        employee.setEmail("w.runninggoat@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

    private Employee nedFlanderEmployee(final Authority authority) {
        var employeePwd = passwordEncoder.encode("HelloWorld!");
        final var employee = new Employee();
        employee.setFirstName("Ned");
        employee.setLastName("Flanders");
        employee.setEmail("n.flanders@aol.us");
        employee.setPassword(employeePwd);
        employee.setAuthorities(Set.of(authority));
        return employee;
    }

}
