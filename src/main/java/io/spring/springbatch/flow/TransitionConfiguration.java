package io.spring.springbatch.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ExitStatus;
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
public class TransitionConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean
    public Job transitionJob() {
        return new JobBuilder("transitionJob", jobRepository)
                .start(transitionStep1())
                    .on("FAILED").to(transitionStep2())
                    .on("FAILED").stop()
                .from(transitionStep1())
                    .on("*").to(transitionStep3())
                .next(transitionStep4())
                .from(transitionStep2()).on("*").to(transitionStep5())
                .end() // SimpleFlow 생성
                .build();
    }

    @Bean
    public Step transitionStep1() {
        return new StepBuilder("transitionStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("transitionStep1 has executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }

    @Bean
    public Step transitionStep2() {
        return new StepBuilder("transitionStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("transitionStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step transitionStep3() {
        return new StepBuilder("transitionStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("transitionStep3 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step transitionStep4() {
        return new StepBuilder("transitionStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("transitionStep4 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step transitionStep5() {
        return new StepBuilder("transitionStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("transitionStep5 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

}
