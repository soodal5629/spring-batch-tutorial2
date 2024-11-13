package io.spring.springbatch.chunk.itemreader.db;

import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JpaPagingReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;

    @Bean
    public Job jpaPagingReaderJob() {
        return new JobBuilder("jpaPagingReaderJob", jobRepository)
                .start(jpaPagingReaderStep())
                .build();
    }

    @Bean
    public Step jpaPagingReaderStep() {
        return new StepBuilder("jpaPagingReaderStep", jobRepository)
                .<Customer, Customer>chunk(5, transactionManager)
                .reader(jpaPagingItemReader())
                .writer(jpaPagingReaderItemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jpaPagingItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        return new JpaPagingItemReaderBuilder<Customer>()
                .name("JpaPagingItemReader")
                .entityManagerFactory(entityManagerFactory)
                .pageSize(5)
                //.queryString("select c.firstName, c.lastName, c.birthdate, c.address from Customer c where firstName like :firstname")
                // fetch join 사용
                .queryString("select c from Customer c join fetch c.address")
                //.parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemWriter<Customer> jpaPagingReaderItemWriter() {
        return items -> {
            for(Customer item : items) {
                log.info("item = {}", item);
            }
//            Iterator iter = items.iterator();
//            while(iter.hasNext()) {
//                Object[] item = (Object[]) iter.next();
//                log.info("jpa paging read item firstName, location = {} {}"
//                        , item[0], item[3]);
//            }
            log.info("==================");
        };
    }
}
