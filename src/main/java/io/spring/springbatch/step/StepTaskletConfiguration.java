package io.spring.springbatch.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * tasklet 개요 Configuration
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class StepTaskletConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepTaskletJob() {
        return new JobBuilder("stepTaskletJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepTasklet1())
                .next(stepTasklet2())
                .build();
    }
    @Bean
    public Step stepTasklet1() {
        return new StepBuilder("stepTasklet1", jobRepository)
                // 익명 클래스
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepTasklet1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step stepTasklet2() {
        return new StepBuilder("stepTasklet2", jobRepository)
                // 구현 클래스
                .tasklet(new CustomTasklet(), transactionManager)
                .build();
    }
}
