package org.holiday.api;

import org.holiday.api.vm.HolidaySearchCriteriaVM;
import org.holiday.api.vm.HolidaySuccessVM;
import org.holiday.api.vm.HolidayVM;
import org.holiday.api.vm.ReplaceHolidayVM;
import org.holiday.batch.FileEmployeeDaysOffJobLauncher;
import org.holiday.domain.TrivalHolidayService;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@RestController
@RequestMapping(path = "/api/holiday", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class TrivalHolidayController {

    @Autowired
    TrivalHolidayService holidayService;

    @Autowired
    FileEmployeeDaysOffJobLauncher fileEmployeeDaysOffJobLauncher;

    @GetMapping
    public ResponseEntity<?> list(Optional<HolidaySearchCriteriaVM> searchCriteria) {
        var searchCriteraOrDefault = searchCriteria.orElse(HolidaySearchCriteriaVM.defaultSearch());
        var employeeDayOff = holidayService.findDayOfByCriteria(searchCriteraOrDefault);
        return ResponseEntity.ok(employeeDayOff);
    }

    @PostMapping
    public ResponseEntity<?> addDayOff(@Valid @RequestBody HolidayVM holidayViewModel) {
        holidayService.addDayOff(holidayViewModel.getEmployeeEmail(), holidayViewModel.getDayOff());
        var message = String.format("Day off ( %s ) successfully added.", holidayViewModel.getDayOff());
        return ResponseEntity.ok(new HolidaySuccessVM(message));
    }

    @PutMapping(path = "{id}")
    public ResponseEntity<?> replaceDayOff(@Valid @RequestBody ReplaceHolidayVM replaceHolidayVM) {
        holidayService.replaceDayOffBy(
                replaceHolidayVM.getEmployeeEmail(),
                replaceHolidayVM.getPreviousDayOff(),
                replaceHolidayVM.getNewDayOff()
        );
        return ResponseEntity.badRequest().build();
    }

    @DeleteMapping
    public ResponseEntity<?> delete(@Valid @RequestBody HolidayVM holidayViewModel) {
        holidayService.removeDayOff(holidayViewModel.getEmployeeEmail(), holidayViewModel.getDayOff());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/dump")
    public ResponseEntity<?> dumpEmployeeJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        fileEmployeeDaysOffJobLauncher.start();
        return ResponseEntity.noContent().build();
    }
}
