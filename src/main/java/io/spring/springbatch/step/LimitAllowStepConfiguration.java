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
public class LimitAllowStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job limitAllowJob() {
        return new JobBuilder("limitAllowJob", jobRepository)
                .start(limitAllowStep1())
                .next(limitAllowStep2())
                .build();
    }

    @Bean
    public Step limitAllowStep1() {
        return new StepBuilder("limitAllowStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepContribution = {}, chunkContext = {}", contribution, chunkContext);
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step limitAllowStep2() {
        return new StepBuilder("limitAllowStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepContribution = {}, chunkContext = {}", contribution, chunkContext);
                    // job 재실행 위해 고의로 예외 발생
                    throw new RuntimeException("limitAllowStep2 was failed");
                }, transactionManager)
                .startLimit(2) // 2번 초과하여 실행할 경우 에러 발생
                .build();
    }
}
