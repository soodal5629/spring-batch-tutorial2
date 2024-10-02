package io.spring.springbatch.simplejob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
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
public class SimpleBasicJobConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job simpleBasicJob() {
        return new JobBuilder("simpleBasicJob", jobRepository)
                .start(simpleBasicStep1())
                .next(simpleBasicStep2())
                .next(simpleBasicStep3())
                .incrementer(new RunIdIncrementer())
                .validator(new JobParametersValidator() {
                    @Override
                    public void validate(JobParameters parameters) throws JobParametersInvalidException {

                    }
                })
                .preventRestart()
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        JobExecutionListener.super.beforeJob(jobExecution);
                    }

                    @Override
                    public void afterJob(JobExecution jobExecution) {
                        JobExecutionListener.super.afterJob(jobExecution);
                    }
                })
                .build();
    }

    @Bean
    public Step simpleBasicStep1() {
        return new StepBuilder("simpleBasicStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleBasicStep1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleBasicStep2() {
        return new StepBuilder("simpleBasicStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleBasicStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step simpleBasicStep3() {
        return new StepBuilder("simpleBasicStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // batch status 변경
                    chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED);
                    contribution.setExitStatus(ExitStatus.STOPPED);
                    log.info("simpleBasicStep3 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
