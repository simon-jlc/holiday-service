package org.holiday.batch;

import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JobScheduler {

    @Autowired
    private FileEmployeeDaysOffJobLauncher fileEmployeeDaysOffJobLauncher;

    //    @Scheduled(fixedDelay = 1000)
    public void startEmployeeDaysOffJob() throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        fileEmployeeDaysOffJobLauncher.start();
    }
}
