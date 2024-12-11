package io.spring.springbatch.practicalexmaple.job.api;

import io.spring.springbatch.practicalexmaple.listener.JobListener;
import io.spring.springbatch.practicalexmaple.tasklet.ApiStartTasklet;
import io.spring.springbatch.practicalexmaple.tasklet.ApiEndTasklet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final ApiStartTasklet apiStartTasklet;
    private final ApiEndTasklet apiEndTasklet;
    private final Step apiJobStep;

    @Bean
    public Job apiJob() {
        return new JobBuilder("apiJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .listener(new JobListener())
                .start(apiStep1())
                .next(apiJobStep)
                .next(apiStep2())
                .build();
    }

    @Bean
    public Step apiStep1() {
        return new StepBuilder("apiStep1", jobRepository)
                .tasklet(apiStartTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
    @Bean
    public Step apiStep2() {
        return new StepBuilder("apiStep2", jobRepository)
                .tasklet(apiEndTasklet, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
}
