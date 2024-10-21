package io.spring.springbatch.chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemReaderWriterProcessorConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job itemReaderWriterProcessorJob() {
        return new JobBuilder("itemReaderWriterProcessorJob", jobRepository)
                .start(itemReaderWriterProcessorStep1())
                .build();
    }

    @Bean
    public Step itemReaderWriterProcessorStep1() {
        return new StepBuilder("itemReaderWriterProcessorStep1", jobRepository)
                .<Customer, Customer>chunk(3, transactionManager)
                .reader(itemReader())
                .processor(itemProcessor())
                .writer(itemWriter())
                .build();
    }

    @Bean
    public ItemReader<Customer> itemReader() {
        return new CustomItemReader(Arrays.asList(new Customer("user1")
                , new Customer("user2"), new Customer("user3")));
    }

    @Bean
    public ItemProcessor<Customer, Customer> itemProcessor() {
        return new CustomItemProcessor();
    }

    @Bean
    public ItemWriter<Customer> itemWriter() {
        return new CustomItemWriter();
    }
}
