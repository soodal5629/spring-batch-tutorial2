package io.spring.springbatch.step;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
@Slf4j
public class StepContributionConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean
    public Job stepContributionJob() {
        return new JobBuilder("stepContributionJob", jobRepository)
                .start(stepContribution1())
                .next(stepContribution2())
                .build();
    }

    @Bean
    public Step stepContribution1() {
        return new StepBuilder("stepContribution1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">> stepContribution1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step stepContribution2() {
        return new StepBuilder("stepContribution2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">> stepContribution2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
}
