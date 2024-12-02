package io.spring.springbatch.chunk.async;

import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.support.builder.SynchronizedItemStreamReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SynchronizedItemStreamReaderConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job synchronizedItemStreamReaderJob() {
        return new JobBuilder("synchronizedItemStreamReaderJob", jobRepository)
                .start(synchronizedItemStreamReaderStep())
                .build();
    }

    @Bean
    public Step synchronizedItemStreamReaderStep() {
        return new StepBuilder("synchronizedItemStreamReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(nonSynchronizedItemStreamReader())
                .listener(new ItemReadListener<Customer>() {
                    @Override
                    public void afterRead(Customer item) {
                        log.info("Thread : {}, item.getId() = {}", Thread.currentThread().getName(), item.getId());
                    }
                })
                .writer(synchronizedItemStreamWriter())
                .taskExecutor(synchronizedTaskExecutor())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<Customer> nonSynchronizedItemStreamReader() {
        JdbcCursorItemReader<Customer> notSafetyReader = new JdbcCursorItemReaderBuilder<Customer>()
                .fetchSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .sql("select id, firstName, lastName, birthdate from customer order by id desc")
                .name("NotSafetyReader")
                .build();
        return new SynchronizedItemStreamReaderBuilder<Customer>()
                .delegate(notSafetyReader)
                .build();
    }

    @Bean
    @StepScope
    public JdbcBatchItemWriter<Customer> synchronizedItemStreamWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

    @Bean
    public TaskExecutor synchronizedTaskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4);
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("safety-reader-thread-");
        return taskExecutor;
    }
}
