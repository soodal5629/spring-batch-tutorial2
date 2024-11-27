package io.spring.springbatch.chunk.async;

import io.spring.springbatch.chunk.async.listener.StopWatchJobListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;

import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ParallelStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job parallelStepJob() {
        return new JobBuilder("parallelStepJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(parallelFlow1())
                //.next(parallelFlow2())
                .split(parallelTaskExecutor()).add(parallelFlow2())
                .end()
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Flow parallelFlow1() {
        TaskletStep parallelFlow1Step1 = new StepBuilder("parallelFlow1Step1", jobRepository)
                .tasklet(commonTasklet(), transactionManager)
                .build();

        return new FlowBuilder<Flow>("parallelFlow")
                .start(parallelFlow1Step1)
                .build();
    }

    @Bean
    public Flow parallelFlow2() {
        TaskletStep parallelFlow2Step2 = new StepBuilder("parallelFlow2Step2", jobRepository)
                .tasklet(commonTasklet(), transactionManager)
                .build();

        TaskletStep parallelFlow2Step3 = new StepBuilder("parallelFlow2Step3", jobRepository)
                .tasklet(commonTasklet(), transactionManager)
                .build();

        return new FlowBuilder<Flow>("parallelFlow")
                .start(parallelFlow2Step2)
                .next(parallelFlow2Step3)
                .build();
    }

    // 여러 step에서 공통으로 사용할 tasklet
    @Bean
    public Tasklet commonTasklet() {
        return new CommonTasklet();
    }

    @Bean
    public TaskExecutor parallelTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(2);
        taskExecutor.setMaxPoolSize(4);
        taskExecutor.setThreadNamePrefix("async-parallel-step-");

        return taskExecutor;
    }

}
