package io.spring.springbatch.flow;

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
public class SimpleFlowConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * FlowJob 구성
     */
    @Bean
    public Job simpleFlowJob() {
        return new JobBuilder("simpleFlowJob", jobRepository)
                .start(simpleFlow1())
                    .on("COMPLETED").to(simpleFlow2())
                .from(simpleFlow1())
                    .on("FAILED").to(simpleFlow3())
                .end() // start ~ end: SimpleFlow 객체 생성
                .build();
    }

    @Bean
    public Flow simpleFlow1() {
        return new FlowBuilder<Flow>("simpleFlow1")
                .start(simpleFlowStep1())
                .next(simpleFlowStep2())
                .end();
    }
    @Bean
    public Flow simpleFlow2() {
        return new FlowBuilder<Flow>("simpleFlow2")
                .start(simpleFlow3())
                .next(simpleFlowStep5())
                .next(simpleFlowStep6())
                .end();
    }
    @Bean
    public Flow simpleFlow3() {
        return new FlowBuilder<Flow>("simpleFlow3")
                .start(simpleFlowStep3())
                .next(simpleFlowStep4())
                .end();
    }
    @Bean
    public Step simpleFlowStep1() {
        return new StepBuilder("simpleFlowStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
    @Bean
    public Step simpleFlowStep2() {
        return new StepBuilder("simpleFlowStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep2 has executed");
                    throw new RuntimeException("simpleFlowStep2 has failed!!!");
                    //return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
    @Bean
    public Step simpleFlowStep3() {
        return new StepBuilder("simpleFlowStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep3 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
    @Bean
    public Step simpleFlowStep4() {
        return new StepBuilder("simpleFlowStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep4 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
    @Bean
    public Step simpleFlowStep5() {
        return new StepBuilder("simpleFlowStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep5 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
    @Bean
    public Step simpleFlowStep6() {
        return new StepBuilder("simpleFlowStep6", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("simpleFlowStep6 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).allowStartIfComplete(true).build();
    }
}
