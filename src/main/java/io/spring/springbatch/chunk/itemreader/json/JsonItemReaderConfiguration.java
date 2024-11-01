package io.spring.springbatch.chunk.itemreader.json;

import io.spring.springbatch.chunk.itemreader.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.batch.item.json.builder.JsonItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JsonItemReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job jsonItemReaderJob() {
        return new JobBuilder("jsonItemReaderJob", jobRepository)
                .start(jsonItemReaderStep())
                .build();
    }

    @Bean
    public Step jsonItemReaderStep() {
        return new StepBuilder("jsonItemReaderStep", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(jsonCustomItemReader())
                .writer(jsonCustomerItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jsonCustomItemReader() {
        return new JsonItemReaderBuilder<Customer>()
                .name("jsonReader")
                .resource(new ClassPathResource("customer.json"))
                .jsonObjectReader(new JacksonJsonObjectReader<>(Customer.class))
                .build();
    }

    @Bean
    public ItemWriter<Customer> jsonCustomerItemWriter() {
        return items -> {
            for(Customer item : items) {
                log.info("item = {}", item.toString());
            }
        };
    }
}
