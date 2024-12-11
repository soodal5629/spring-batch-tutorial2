package io.spring.springbatch.practicalexmaple.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;

import java.time.Duration;
import java.time.LocalTime;

@Slf4j
public class JobListener implements JobExecutionListener {
    @Override
    public void beforeJob(JobExecution jobExecution) {

    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        LocalTime startTime = jobExecution.getStartTime().toLocalTime();
        LocalTime endTime = jobExecution.getEndTime().toLocalTime();
        long time = Duration.between(startTime, endTime).toMillis();
        log.info("================================");
        log.info("####### 총 소요 시간 = {}", time);
        log.info("================================");
    }
}
