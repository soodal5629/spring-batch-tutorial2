package io.spring.springbatch.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.*;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.job.DefaultJobParametersExtractor;
import org.springframework.batch.core.step.job.JobParametersExtractor;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JobStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job parentJob() {
        return new JobBuilder("parentJob", jobRepository)
                .start(jobStep(null))
                .next(jobStep2())
                .incrementer(new RunIdIncrementer())
                .build();
    }

    @Bean
    public Step jobStep(JobLauncher jobLauncher) {
        return new StepBuilder("jobStep", jobRepository)
                .job(childJob())
                .launcher(jobLauncher)
                .parametersExtractor(jobParameterExtractor())
                .listener(new StepExecutionListener() {
                    @Override
                    public void beforeStep(StepExecution stepExecution) {
                        stepExecution.getExecutionContext().put("name", "user123");
                    }
                })
                .build();
    }

    private JobParametersExtractor jobParameterExtractor() {
        // ExecutionContext 안에 저장된 key, value를 참조하여 데이터를 가져올 수 있음
        DefaultJobParametersExtractor extractor = new DefaultJobParametersExtractor();
        extractor.setKeys(new String [] {"name"});
        return extractor;
    }

    @Bean
    public Job childJob() {
        return new JobBuilder("childJob", jobRepository)
                .start(childJobStep())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(JobExecution jobExecution) {
                        log.info("childJob parameter name's value = {}", jobExecution.getJobParameters().getString("name"));
                    }
                })
                .build();
    }

    @Bean
    public Step jobStep2() {
        return new StepBuilder("jobStep2", jobRepository)
                .tasklet((contribution, chunkContext) ->  {
                    throw new RuntimeException("jobStep2 error");
                    //return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    @Bean
    public Step childJobStep() {
        return new StepBuilder("childJobStep", jobRepository)
                .tasklet((contribution, chunkContext) ->  {
                    //throw new RuntimeException("childJobStep error");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }
}
