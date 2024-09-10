package io.spring.springbatch;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobParameterConfiguration {
    @Bean
    public Job parameterJob(JobRepository jobRepository, Step parameterStep1, Step parameterStep2) {
        return new JobBuilder("parameterJob", jobRepository)
                .start(parameterStep1)
                .next(parameterStep2).build();
    }

    @Bean
    public Step parameterStep1(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("parameterStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // contribution 이용해서 파라미터 확인
                    JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
                    jobParameters.getString("name");
                    jobParameters.getLong("seq");
                    jobParameters.getDate("date");
                    jobParameters.getDouble("age");
                    log.info("parameterStep1 has executed");
                    // map 이용해서 파라미터 확인
                    Map<String, Object> jobParameters2 = chunkContext.getStepContext().getJobParameters();

                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }

    @Bean
    public Step parameterStep2(JobRepository jobRepository, PlatformTransactionManager platformTransactionManager) {
        return new StepBuilder("parameterStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("parameterStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, platformTransactionManager)
                .build();
    }
}
