package io.spring.springbatch.simplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
public class ValidatorConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job validatorJob() {
        return new JobBuilder("validatorJob", jobRepository)
                .start(validatorStep1())
                .next(validatorStep2())
                .next(validatorStep3())
                .validator(new CustomJobParametersValidator())
                .build();
    }

    @Bean
    public Step validatorStep1() {
        return new StepBuilder("validatorStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("validatorStep1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step validatorStep2() {
        return new StepBuilder("validatorStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("validatorStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step validatorStep3() {
        return new StepBuilder("validatorStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("validatorStep3 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
