package io.spring.springbatch.chunk.itemreader.db;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcCursorReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private int chunkSize = 10;
    private final DataSource dataSource;

    @Bean
    public Job jdbcCursorReaderJob() {
        return new JobBuilder("jdbcCursorReaderJob", jobRepository)
                .start(jdbcCursorReaderStep())
                .build();
    }

    @Bean
    public Step jdbcCursorReaderStep() {
        return new StepBuilder("jdbcCursorReaderStep", jobRepository)
                .<Customer, Customer>chunk(chunkSize, transactionManager)
                .reader(jdbcCursorItemReader())
                .writer(jdbcCursorItemCursorWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemReader<Customer> jdbcCursorItemReader() {
        return new JdbcCursorItemReaderBuilder<Customer>()
                .name("jdbcCursorItemReader")
                .fetchSize(chunkSize)
                .sql("select * from customer where firstName like ? order by lastName, firstName")
                .queryArguments("R%")
                .beanRowMapper(Customer.class)
                .dataSource(dataSource)
                .build();
    }

    @Bean
    public ItemWriter<Customer> jdbcCursorItemCursorWriter() {
        return items -> {
            for(Customer item : items) {
                log.info("item = {}", item.toString());
            }
            log.info("==========================");
        };
    }
}
