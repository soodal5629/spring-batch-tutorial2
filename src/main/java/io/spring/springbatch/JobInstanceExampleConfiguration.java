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

@Slf4j
@Configuration
public class JobInstanceExampleConfiguration {
    @Bean
    public Job jobInstanceJob(Step jobInstanceStep1, Step jobInstanceStep2, JobRepository jobRepository) {
        return new JobBuilder("jobInstanceJob", jobRepository)
                .start(jobInstanceStep1)
                .next(jobInstanceStep2)
                .build();
    }

    @Bean
    public Step jobInstanceStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("jobInstanceStep1", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("### jobInstanceStep1 executed!");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }
    @Bean
    public Step jobInstanceStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("jobInstanceStep2", jobRepository)
                .tasklet(((contribution, chunkContext) -> {
                    log.info("### jobInstanceStep2 executed!");
                    return RepeatStatus.FINISHED;
                }), platformTransactionManager)
                .build();
    }
}

