package io.spring.springbatch.chunk.async;

import io.spring.springbatch.chunk.itemWriter.db.jpa.Customer2;
import io.spring.springbatch.chunk.itemWriter.xml.CustomerRowMapper;
import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class AsyncConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job asyncJob() throws InterruptedException {
        return new JobBuilder("asyncJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(syncStep())
                //.start(asyncStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step syncStep() throws InterruptedException {
        return new StepBuilder("syncStep", jobRepository)
                .<Customer, Customer2>chunk(20, transactionManager)
                .reader(syncPagingItemReader())
                .processor(customItemProcessor())
                .writer(customItemWriter())
                .build();
    }

    @Bean
    public Step asyncStep() throws InterruptedException {
        return new StepBuilder("asyncStep", jobRepository)
                .<Customer, Customer2>chunk(20, transactionManager)
                .reader(syncPagingItemReader())
                .processor(asyncItemProcessor())
                .writer(asyncItemWriter())
                .build();
    }

    @Bean
    public ItemProcessor<Customer, Customer2> customItemProcessor() throws InterruptedException {
        return new ItemProcessor<Customer, Customer2>() {
            @Override
            public Customer2 process(Customer item) throws Exception {
                Thread.sleep(30);
                return new Customer2(item.getId(), item.getFirstName().toUpperCase()
                        , item.getLastName().toUpperCase(), item.getBirthdate());
            }
        };
    }

    @Bean
    public AsyncItemProcessor asyncItemProcessor() throws InterruptedException {
        // AsyncItemProcessor -> 비동기 실행 환경을 제공하며 ItemProcessor에게 작업 위임
        AsyncItemProcessor<Customer, Customer2> asyncItemProcessor = new AsyncItemProcessor<>();
        asyncItemProcessor.setDelegate(customItemProcessor());
        asyncItemProcessor.setTaskExecutor(new SimpleAsyncTaskExecutor());

        return asyncItemProcessor;
    }

    @Bean
    public AsyncItemWriter asyncItemWriter() {
        AsyncItemWriter<Customer2> writer = new AsyncItemWriter<>();
        writer.setDelegate(customItemWriter());

        return writer;
    }

    @Bean
    public JdbcPagingItemReader<Customer> syncPagingItemReader() {
        JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

        reader.setDataSource(dataSource);
        reader.setFetchSize(50);
        reader.setRowMapper(new CustomerRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        reader.setQueryProvider(queryProvider);

        return reader;
    }

    @Bean
    public JdbcBatchItemWriter customItemWriter() {
        JdbcBatchItemWriter<Customer2> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }

}
