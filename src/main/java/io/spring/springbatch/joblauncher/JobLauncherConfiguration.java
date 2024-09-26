package io.spring.springbatch.joblauncher;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@Slf4j
@RequiredArgsConstructor
public class JobLauncherConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jobLauncherJob() {
        return new JobBuilder("jobLauncherJob", jobRepository)
                .start(jobLauncherStep1())
                .next(jobLauncherStep2())
                .build();
    }
    @Bean
    public Step jobLauncherStep1() {
        return new StepBuilder("jobLauncherStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    // 동기/비동기 확인하기 위해 시간차를 둠
                    Thread.sleep(3000L);
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
    @Bean
    public Step jobLauncherStep2() {
        return new StepBuilder("jobLauncherStep1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    return RepeatStatus.FINISHED;
                }, transactionManager).build();
    }
}
