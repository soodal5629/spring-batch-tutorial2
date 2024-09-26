package io.spring.springbatch.jobrepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecutionListener;
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
@RequiredArgsConstructor
public class JobRepositoryConfiguration {
    private final JobRepository customJobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobExecutionListener jobRepositoryListener;
    @Bean
    public Job jobRepositoryJob() {
        return new JobBuilder("jobRepositoryJob", customJobRepository)
                .start(jobRepositoryStep1())
                .next(jobRepositoryStep2())
                .listener(jobRepositoryListener)
                .build();
    }
    @Bean
    public Step jobRepositoryStep1() {
        return new StepBuilder("jobRepositoryStep1", customJobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
    @Bean
    public Step jobRepositoryStep2() {
        return new StepBuilder("jobRepositoryStep2", customJobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
