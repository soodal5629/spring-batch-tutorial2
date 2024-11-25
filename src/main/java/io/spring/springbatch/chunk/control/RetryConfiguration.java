package io.spring.springbatch.chunk.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryException;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryBasicJob() {
        return new JobBuilder("retryBasicJob", jobRepository)
                .start(retryBasicStep())
                .build();
    }

    @Bean
    public Step retryBasicStep() {
        return new StepBuilder("retryBasicStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(retryItemReader())
                .processor(retryBasicProcessor())
                .writer(items -> items.forEach(item -> log.info("retry basic writer item = {}", item)))
                .faultTolerant()
//                .retry(RetryableException.class)
//                .retryLimit(2)
                // custom RetryPolicy 사용
                .retryPolicy(customRetryPolicy())
                .skip(RetryableException.class)
                .skipLimit(2)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> retryBasicProcessor() {
        return new RetryBasicItemProcessor();
    }

    @Bean
    public ItemReader<String> retryItemReader() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryPolicy customRetryPolicy() {
        // 예외 등록
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);
        RetryPolicy retryPolicy = new SimpleRetryPolicy(2, exceptionClass);
        return retryPolicy;
    }
}
