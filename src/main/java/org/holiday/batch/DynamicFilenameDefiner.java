package org.holiday.batch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;

import java.text.SimpleDateFormat;

@Slf4j
public class DynamicFilenameDefiner implements StepExecutionListener {

    @Override
    public void beforeStep(final StepExecution stepExecution) {
        var startTime = stepExecution.getStartTime();
        var jobName = stepExecution.getJobExecution().getJobInstance().getJobName();
        var jobInstanceId = stepExecution.getJobExecution().getJobInstance().getInstanceId();
        var dateFormat = new SimpleDateFormat("yyyy-MM-dd_HHmmssSSS");
        var outputFilename = jobName + "_" + jobInstanceId + "_" + dateFormat.format(startTime) + ".csv";
        stepExecution.getExecutionContext().put("output.file.name", outputFilename);
        log.info("----> Define filename {}", outputFilename);
    }

    @Override
    public ExitStatus afterStep(final StepExecution stepExecution) {
        return stepExecution.getExitStatus();
    }
}
