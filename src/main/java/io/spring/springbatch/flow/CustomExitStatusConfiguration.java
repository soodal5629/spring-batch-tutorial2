package io.spring.springbatch.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
public class CustomExitStatusConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean
    public Job customExitStatusJob() {
        return new JobBuilder("customExitStatusJob", jobRepository)
                .start(customExitStatusStep1())
                    .on("FAILED")
                    .to(customExitStatusStep2())
                    .on("PASS").stop()
                .end() // SimpleFlow 생성
                .build();
    }

    @Bean
    public Step customExitStatusStep1() {
        return new StepBuilder("customExitStatusStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("customExitStatusStep1 has executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }

    @Bean
    public Step customExitStatusStep2() {
        return new StepBuilder("customExitStatusStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("customExitStatusStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .listener(new PassCheckingListener())
                .build();
    }
}
