package io.spring.springbatch.practicalexmaple.job.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiJobChildConfiguration {
    private final JobRepository jobRepository;
    private final Step apiMasterStep;
    // JobStep은 JobLauncher 가 필요함
    private final JobLauncher jobLauncher;

    @Bean
    public Step apiJobStep() {
        return new StepBuilder("apiJobStep", jobRepository)
                .job(apiChildJob())
                .launcher(jobLauncher)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Job apiChildJob() {
        return new JobBuilder("apiChildJob", jobRepository)
                .start(apiMasterStep)
                .build();
    }
}
