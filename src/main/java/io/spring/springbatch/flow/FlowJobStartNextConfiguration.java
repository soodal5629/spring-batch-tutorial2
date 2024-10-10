package io.spring.springbatch.flow;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
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
public class FlowJobStartNextConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    /**
     * Flow 구성이지만 조건에 따른 전환은 없음
     * -> 하나의 Step 혹은 Flow에 예외 발생 시 flowJobStartNext(Job) 도 실패함
     */
    @Bean
    public Job flowJobStartNext() {
        return new JobBuilder("flowJobStartNext", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(flowA())
                .next(flowJobStartNextStep3())
                .next(flowB())
                .next(flowJobStartNextStep6())
                .end()
                .build();
    }
    @Bean
    public Flow flowA() {
        return new FlowBuilder<Flow>("flowA")
                .start(flowJobStartNextStep1())
                .next(flowJobStartNextStep2())
                .end();
    }
    @Bean
    public Flow flowB() {
        return new FlowBuilder<Flow>("flowB")
                .start(flowJobStartNextStep4())
                .next(flowJobStartNextStep5())
                .end();
    }
    @Bean
    public Step flowJobStartNextStep1() {
        return new StepBuilder("flowJobStartNextStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step flowJobStartNextStep2() {
        return new StepBuilder("flowJobStartNextStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep2 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step flowJobStartNextStep3() {
        return new StepBuilder("flowJobStartNextStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep3 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step flowJobStartNextStep4() {
        return new StepBuilder("flowJobStartNextStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep4 has executed");
                    throw new RuntimeException("flowJobStartNextStep4 failed");
                    //return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step flowJobStartNextStep5() {
        return new StepBuilder("flowJobStartNextStep5", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep5 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step flowJobStartNextStep6() {
        return new StepBuilder("flowJobStartNextStep6", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("flowJobStartNextStep6 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

}
