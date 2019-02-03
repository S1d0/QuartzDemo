package com.multi.job.first;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.time.LocalDateTime;

@Slf4j
public class PrintJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        LocalDateTime dateTime = LocalDateTime.now();
        log.info(" PRINT JOB EXECUTE  TIME :{}",dateTime);
    }
}
