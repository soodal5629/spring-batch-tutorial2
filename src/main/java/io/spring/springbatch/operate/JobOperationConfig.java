package io.spring.springbatch.operate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.JobRegistryBeanPostProcessor;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobOperationConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final JobRegistry jobRegistry;

    @Bean
    public Job jobOperatorJob() {
        return new JobBuilder("jobOperatorJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(jobOperatorStep())
                .next(jobOperatorStep2())
                .build();
    }

    @Bean
    public Step jobOperatorStep() {
        return new StepBuilder("jobOperatorStep", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### jobOperatorStep has executed");
                    JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                    log.info("### param = {}", jobParameters.getString("id"));
                    Thread.sleep(10000); //
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }
    @Bean
    public Step jobOperatorStep2() {
        return new StepBuilder("jobOperatorStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### jobOperatorStep2 has executed");
                    JobParameters jobParameters = contribution.getStepExecution().getJobParameters();
                    Thread.sleep(10000); // 10초 소요
                    log.info("!!!! step2 finished");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public BeanPostProcessor jobRegistryBeanPostProcessor() {
        JobRegistryBeanPostProcessor jobRegistryBeanPostProcessor = new JobRegistryBeanPostProcessor();
        jobRegistryBeanPostProcessor.setJobRegistry(jobRegistry);
        return jobRegistryBeanPostProcessor;
    }
}
