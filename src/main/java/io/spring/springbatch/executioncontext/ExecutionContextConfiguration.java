package io.spring.springbatch.executioncontext;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ExecutionContextConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job executionContextJob() {
        return new JobBuilder("executionContextJob", jobRepository)
                .start(executionContextStep1())
                .next(executionContextStep2())
                .next(executionContextStep3()) // 여기서 고의로 예외 발생 -> job 재실행 가능
                .next(executionContextStep4())
                .build();
    }

    @Bean
    public Step executionContextStep1() {
        return new StepBuilder("executionContextStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### executionContextStep1 was executed");
                    // contribution, chunkContext에서 동일하게 ExecutionContext를 얻을 수 있음
                    ExecutionContext jobExecutionContext = contribution.getStepExecution().getJobExecution().getExecutionContext();
                    ExecutionContext stepExecutionContext = contribution.getStepExecution().getExecutionContext();

                    String jobName = chunkContext.getStepContext().getStepExecution().getJobExecution().getJobInstance().getJobName();
                    String stepName = chunkContext.getStepContext().getStepName();
                    if(jobExecutionContext.get("jobName") == null) { // 처음 실행되는 경우 put -> db 저장
                        jobExecutionContext.put("jobName", jobName);
                    }
                    if(stepExecutionContext.get("stepName") == null) { // 처음 실행되는 경우 put -> db 저장
                        stepExecutionContext.put("stepName", stepName);
                    }
                    log.info("### 1 jobName = {}", jobExecutionContext.get("jobName"));
                    log.info("### 1 stepName = {}", stepExecutionContext.get("stepName"));
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step executionContextStep2() {
        return new StepBuilder("executionContextStep2", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### executionContextStep2 was executed");
                    ExecutionContext jobExecutionContext = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext();
                    ExecutionContext stepExecutionContext = chunkContext.getStepContext().getStepExecution().getExecutionContext();
                    log.info("### 2 jobName = {}", jobExecutionContext.get("jobName"));
                    log.info("### 2 stepName = {}", stepExecutionContext.get("stepName"));
                    String stepName = chunkContext.getStepContext().getStepExecution().getStepName();
                    if(stepExecutionContext.get("stepName") == null) {
                        stepExecutionContext.put("stepName", stepName);
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step executionContextStep3() {
        return new StepBuilder("executionContextStep3", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### executionContextStep3 was executed");
                    Object name = chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name");
                    // db에 context 데이터가 잘 저장되어있다면 name이 null이 아니므로 if문을 타지않으므로 예외도 발생하지 않음
                    if(name == null) {
                        // 얘외 발생해도 db에 저장되는지 확인
                        chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().put("name", "user1");
                        throw new RuntimeException("step3 was failed");
                    }
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step executionContextStep4() {
        return new StepBuilder("executionContextStep4", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("### executionContextStep4 was executed");
                    // 세번째 step에서 ExecutionContext에 저장한 값이 db에 저장되었는지 확인
                    log.info("name: {}", chunkContext.getStepContext().getStepExecution().getJobExecution().getExecutionContext().get("name"));
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }

}
