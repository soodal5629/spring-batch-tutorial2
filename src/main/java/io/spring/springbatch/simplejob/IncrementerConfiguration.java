package io.spring.springbatch.simplejob;

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

@Configuration
@Slf4j
@RequiredArgsConstructor
public class IncrementerConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job incrementerJob() {
        return new JobBuilder("incrementerJob", jobRepository)
                .start(incrementerStep1())
                .next(incrementerStep2())
                .incrementer(new CustomJobParametersIncrementer())
                //.incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step incrementerStep1() {
        return new StepBuilder("incrementerStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("incrementerStep1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step incrementerStep2() {
        return new StepBuilder("incrementerStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("incrementerStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
