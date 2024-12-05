package io.spring.springbatch.eventlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryListenerConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryListenerJob() {
        return new JobBuilder("retryListenerJob", jobRepository)
                .start(retryListenerStep())
                .build();
    }

    @Bean
    public Step retryListenerStep() {
        return new StepBuilder("retryListenerStep", jobRepository)
                .allowStartIfComplete(true)
                .<Integer, String>chunk(10, transactionManager)
                .reader(retryListItemReader())
                .processor(new CustomRetryListenerItemProcessor())
                .writer(new CustomRetryListenerItemWriter())
                .faultTolerant()
                .retry(CustomRetryException.class)
                .retryLimit(2)
                .listener(new CustomRetryListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> retryListItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4);
        return new LinkedListRetryListenerItemReader<>(list);
    }
}
