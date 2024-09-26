package io.spring.springbatch.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class JobRepositoryListener implements JobExecutionListener {
    private final JobRepository jobRepository;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        JobExecutionListener.super.beforeJob(jobExecution);
    }

    // job 실행 이후 (즉, 마지막으로 실행된) JobExecution 가져오기
    @Override
    public void afterJob(JobExecution jobExecution) {
        log.info("### JobRepository listener start!!");
        String jobName = jobExecution.getJobInstance().getJobName();
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("requestDate", "20240925")
                .toJobParameters();
        // jobRepository 이용해서 db에서 정보 가져오기
        JobExecution lastJobExecution = jobRepository.getLastJobExecution(jobName, jobParameters);
        if(lastJobExecution != null) {
            // stepExecution 목록의 정보 가져와보기
            for(StepExecution stepExecution : lastJobExecution.getStepExecutions()) {
                String stepName = stepExecution.getStepName();
                BatchStatus status = stepExecution.getStatus();
                ExitStatus exitStatus = stepExecution.getExitStatus();
                log.info("### stepName = {}", stepName);
                log.info("### status = {}", status);
                log.info("### exitStatus = {}", exitStatus);

            }
        }
    }
}
