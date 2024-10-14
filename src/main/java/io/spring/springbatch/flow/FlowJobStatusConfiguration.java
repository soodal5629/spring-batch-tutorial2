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
public class FlowJobStatusConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean
    public Job jobStatus() {
        return new JobBuilder("jobStatus", jobRepository)
                .start(flowJobStatusStep1())
                .next(flowJobStatusStep2())
                .build();
    }

    @Bean
    public Job flowJobStatus() {
        return new JobBuilder("flowJobStatus", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(flowJobStatusStep1())
                    .on("FAILED").to(flowJobStatusStep2())
                .end()
                .build();
    }

    @Bean
    public Step flowJobStatusStep1() {
        return new StepBuilder("flowJobStatusStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStatusStep1 has executed");
                    contribution.setExitStatus(ExitStatus.FAILED);
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

    @Bean
    public Step flowJobStatusStep2() {
        return new StepBuilder("flowJobStatusStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStatusStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

}
