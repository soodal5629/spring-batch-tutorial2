package io.spring.springbatch.chunk.async;

import io.spring.springbatch.chunk.async.listener.CustomItemProcessorListener;
import io.spring.springbatch.chunk.async.listener.CustomItemReadListener;
import io.spring.springbatch.chunk.async.listener.CustomItemWriterListener;
import io.spring.springbatch.chunk.async.listener.StopWatchJobListener;
import io.spring.springbatch.chunk.itemWriter.xml.CustomerRowMapper;
import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class MultiThreadStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job multiThreadStepJob() {
        return new JobBuilder("multiThreadStepJob", jobRepository)
                .start(multiThreadStep())
                .listener(new StopWatchJobListener())
                .build();
    }

    @Bean
    public Step multiThreadStep() {
        return new StepBuilder("multiThreadStep", jobRepository)
                .<Customer, Customer>chunk(50, transactionManager)
                .allowStartIfComplete(true)
                .reader(multiThreadItemReader())
                .listener(new CustomItemReadListener())
                .processor((ItemProcessor<Customer, Customer>) item -> item)
                .listener(new CustomItemProcessorListener())
                .writer(multiThreadItemWriter())
                .listener(new CustomItemWriterListener())
                // 비동기 멀티 스레드 설정
                .taskExecutor(multiThreadTaskExecutor())
                .build();
    }

    @Bean
    public TaskExecutor multiThreadTaskExecutor() {
        // 자바에서 제공하는 스레드 풀 관리하는 TaskExecutor - 이거 사용 권장
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(4); // 4개의 스레드 기본적으로 생성
        // 4개의 스레드가 작업을 처리 중일 때 나머지 작업이 남아있을 경우 추가로 생성할 수 있는 스레드 개수
        taskExecutor.setMaxPoolSize(8);
        taskExecutor.setThreadNamePrefix("async-multi-thread");

        return taskExecutor;
    }

    @Bean
    public ItemReader<Customer> multiThreadItemReader() {
        // JdbcPagingItemReader는 thread safe함
        JdbcPagingItemReader<Customer> itemReader = new JdbcPagingItemReader<>();
        itemReader.setDataSource(dataSource);
        itemReader.setFetchSize(50);
        itemReader.setPageSize(50);
        itemReader.setRowMapper(new CustomerRowMapper());

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, firstName, lastName, birthdate");
        queryProvider.setFromClause("from customer");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);
        itemReader.setQueryProvider(queryProvider);

        return itemReader;
    }

    @Bean
    public JdbcBatchItemWriter multiThreadItemWriter() {
        JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
        itemWriter.afterPropertiesSet();

        return itemWriter;
    }
}
