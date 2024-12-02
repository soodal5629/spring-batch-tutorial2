package io.spring.springbatch.eventlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecutionListener;
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
public class JobAndStepListenerConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final StepExecutionListener stepExecutionListener;

    @Bean
    public Job jobAndStepListenerJob() {
        return new JobBuilder("jobAndStepListenerJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jobAndStepListenerStep())
                .next(jobAndStepListenerStep2())
                //.listener(new CustomJobExecutionListener())
                .listener(new CustomJobExecutionAnnotationListener()) // 어노테이션 방식
                .build();
    }

    @Bean
    public Step jobAndStepListenerStep() {
        return new StepBuilder("jobAndStepListenerStep", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .listener(stepExecutionListener) // 인터페이스 방식
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Step jobAndStepListenerStep2() {
        return new StepBuilder("jobAndStepListenerStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED, transactionManager)
                .listener(stepExecutionListener) // 인터페이스 방식
                .allowStartIfComplete(true)
                .build();
    }
}
