package io.spring.springbatch.chunk.control.retrytemplate;

import io.spring.springbatch.chunk.control.RetryBasicItemProcessor;
import io.spring.springbatch.chunk.control.RetryableException;
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
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RetryTemplateConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job retryTemplateJob() {
        return new JobBuilder("retryTemplateJob", jobRepository)
                .start(retryTemplateStep())
                .build();
    }

    @Bean
    public Step retryTemplateStep() {
        return new StepBuilder("retryTemplateStep", jobRepository)
                .<String, Item>chunk(10, transactionManager)
                .reader(retryTemplateItemReader())
                .processor(retryTemplateItemProcessor())
                .writer(items -> items.forEach(item -> log.info("retry template writer item = {}", item)))
                .faultTolerant()
                .retry(RetryableException.class)
                .retryLimit(2)
                .skip(RetryableException.class)
                .skipLimit(2)
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemProcessor<? super String, Item> retryTemplateItemProcessor() {
        return new RetryTemplateItemProcessor(customRetryTemplate());
    }

    @Bean
    public ItemReader<String> retryTemplateItemReader() {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            items.add(String.valueOf(i));
        }
        return new ListItemReader<>(items);
    }

    @Bean
    public RetryTemplate customRetryTemplate() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(RetryableException.class, true);

        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(2000); // 2초

        RetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);

        RetryTemplate retryTemplate = new RetryTemplate();
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        retryTemplate.setBackOffPolicy(backOffPolicy);
        return retryTemplate;
    }
}
