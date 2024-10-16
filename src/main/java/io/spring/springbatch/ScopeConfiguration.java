package io.spring.springbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ScopeConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job scopeJob() {
        return new JobBuilder("scopeJob", jobRepository)
                .start(scopeStep1(null))
                .next(scopeStep2())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        jobExecution.getExecutionContext().putString("name", "jcl");
                    }
                })
                .build();
    }
    @Bean
    @JobScope
    public Step scopeStep1(@Value("#{jobParameters['message']}") String message) {
        log.info("message = {}", message);
        return new StepBuilder("scopeStep1", jobRepository)
                .tasklet(scopeTasklet(null, null), transactionManager)
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().putString("name2", "JCL");
                    }
                })
                .build();
    }

    @Bean
    public Step scopeStep2() {
        return new StepBuilder("scopeStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> scopeStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    @StepScope
    public Tasklet scopeTasklet(@Value("#{jobExecutionContext['name']}") String name
                                , @Value("#{stepExecutionContext['name2']}") String name2) {
        return (stepContribution, chunkContext) -> {
            log.info(">>> scopeTasklet has executed");
            log.info("name = {}, name2 = {}", name, name2);
            return RepeatStatus.FINISHED;
        };
    }
}
