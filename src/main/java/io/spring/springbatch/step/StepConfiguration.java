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
public class StepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepBasicJob() {
        return new JobBuilder("stepBasicJob", jobRepository)
                .start(stepBasic1())
                .next(stepBasic2())
                .build();
    }

    @Bean
    public Step stepBasic1() {
        return new StepBuilder("stepBasic1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepBasic1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepBasic2() {
        return new StepBuilder("stepBasic2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepBasic2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
