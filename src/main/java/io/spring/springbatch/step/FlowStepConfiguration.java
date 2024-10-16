package io.spring.springbatch.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlowStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flowStepJob() {
        return new JobBuilder("flowStepJob", jobRepository)
                .start(flowStep1())
                .next(flowStep3())
                .build();
    }

    @Bean
    public Step flowStep1() {
        return new StepBuilder("flowStep1", jobRepository)
                // FlowStep - ★ Step이므로 내부에서 에러 나면 job도 실패함
                .flow(flowExample1())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public Flow flowExample1() {
        return new FlowBuilder<Flow>("flowExample1")
                .start(flowStep2())
                .end();
    }

    @Bean
    public Step flowStep2() {
        return new StepBuilder("flowStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> flowStep2 executed");
                    throw new RuntimeException("flowStep2 failed");
                    //return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true)
                .build();
    }
    @Bean
    public Step flowStep3() {
        return new StepBuilder("flowStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info(">>> flowStep3 executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true)
                .build();
    }
}
