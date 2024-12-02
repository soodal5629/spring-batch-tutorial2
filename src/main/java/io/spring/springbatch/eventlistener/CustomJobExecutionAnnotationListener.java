package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.batch.core.annotation.AfterJob;
import org.springframework.batch.core.annotation.BeforeJob;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
public class CustomJobExecutionAnnotationListener {
    @BeforeJob
    public void beforeJob(JobExecution jobExecution) {
        log.info("jobExecution.name = {} is started", jobExecution.getJobInstance().getJobName());
    }

    @AfterJob
    public void afterJob(JobExecution jobExecution) {
        LocalTime startTime = jobExecution.getStartTime().toLocalTime();
        LocalTime endTime = jobExecution.getEndTime().toLocalTime();
        long time = Duration.between(startTime, endTime).toMillis();
        log.info("================================");
        log.info("####### 총 소요 시간 = {}", time);
        log.info("================================");
    }
}
