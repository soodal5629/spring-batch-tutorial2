package io.spring.springbatch;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
public class JobExecutionConfiguration {

    @Bean
    public Job jobExecution(JobRepository jobRepository, Step stepExecution1,  Step stepExecution2) {
        return new JobBuilder("jobExecution", jobRepository)
                .start(stepExecution1)
                .next(stepExecution2)
                .build();
    }

    @Bean
    public Step stepExecution1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepExecution1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("### stepExecution1 has executed");
                    return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }

    @Bean
    public Step stepExecution2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("stepExecution2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("### stepExecution2 has executed");
                    // 고의로 예외 던지기 - JobInstance 가 재실행 되는지 확인 위해
                    throw new RuntimeException("step2 has failed");
                    // return RepeatStatus.FINISHED;
                }), transactionManager)
                .build();
    }
}
