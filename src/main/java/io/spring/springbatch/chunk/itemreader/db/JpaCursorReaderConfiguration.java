package io.spring.springbatch.chunk.itemreader.db;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JpaCursorReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaCursorReaderJob() {
        return new JobBuilder("jpaCursorReaderJob", jobRepository)
                .start(jpaCursorReaderStep())
                .build();
    }

    @Bean
    public Step jpaCursorReaderStep() {
        return new StepBuilder("jpaCursorReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jpaCursorItemReader())
                .writer(jpaCursorReaderItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jpaCursorItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JpaCursorItemReaderBuilder<Customer>()
                .name("jpaCursorItemReader")
                .entityManagerFactory(entityManagerFactory)
                .queryString("select c.firstName, c.lastName, c.birthdate from Customer c where firstName like :firstname")
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemWriter<Customer> jpaCursorReaderItemWriter() {
        return items -> {
            //Chunk<? extends Customer>.ChunkIterator iterator = items.iterator();
            Iterator iter = items.iterator();
            while(iter.hasNext()) {
                Object[] item = (Object[]) iter.next();
                log.info("jpa read item firstName, lastName, birthdate = {} {} {}", item[0], item[1], item[2]);
            }
        };
    }
}
