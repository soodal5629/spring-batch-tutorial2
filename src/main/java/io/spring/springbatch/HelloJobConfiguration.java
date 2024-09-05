package io.spring.springbatch;

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
@RequiredArgsConstructor
@Slf4j
public class HelloJobConfiguration {

    @Bean
    public Job helloJob(JobRepository jobRepository, Step helloStep1, Step helloStep2) {
        return new JobBuilder("helloJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(helloStep1)
                .next(helloStep2)
                .build();
    }

    @Bean
    public Step helloStep1(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep1", jobRepository)
                // Step에서는 기본적으로 tasklet을 무한 반복시킴 -> repeat status를 줘야 함
                .tasklet((contribution, chunkContext) -> {
                    log.info("=========================");
                    log.info(" >> Hello Spring Batch!!");
                    log.info("=========================");
                    return RepeatStatus.FINISHED; // return null 과 같음
                }, transactionManager)
                .build();
    }

    @Bean
    public Step helloStep2(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        return new StepBuilder("helloStep2", jobRepository)
                // Step에서는 기본적으로 tasklet을 무한 반복시킴 -> repeat status를 줘야 함
                .tasklet((contribution, chunkContext) -> {
                    log.info("=========================");
                    log.info(" >> step2 was executed!!");
                    log.info("=========================");
                    return RepeatStatus.FINISHED; // return null 과 같음
                }, transactionManager)
                .build();
    }

}
