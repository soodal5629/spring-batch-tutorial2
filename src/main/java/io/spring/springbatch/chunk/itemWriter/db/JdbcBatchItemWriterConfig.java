package io.spring.springbatch.chunk.itemWriter.db;

import io.spring.springbatch.chunk.itemWriter.xml.CustomerRowMapper;
import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcBatchItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job jdbcBatchItemWriterJob() {
        return new JobBuilder("jdbcBatchItemWriterJob", jobRepository)
                .start(jdbcBatchItemWriterStep())
                .build();
    }

    @Bean
    public Step jdbcBatchItemWriterStep() {
        return new StepBuilder("jdbcBatchItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(dbJdbcCustomItemReader())
                .writer(jdbcBatchItemWriter())
                .build();
    }

    // 읽어온 데이터를 새로운 db 테이블인 customer2 에 insert
    @Bean
    public ItemWriter<? super Customer> jdbcBatchItemWriter() {
        return new JdbcBatchItemWriterBuilder<Customer>()
                .dataSource(dataSource)
                .sql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)")
                .beanMapped()
                .build();
    }

    @Bean
    public ItemReader<Customer> dbJdbcCustomItemReader() {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");

        Map<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);

        PostgresPagingQueryProvider pagingQueryProvider = new PostgresPagingQueryProvider();
        pagingQueryProvider.setSelectClause("id, firstName, lastName, birthdate");
        pagingQueryProvider.setFromClause("from customer");
        pagingQueryProvider.setWhereClause("where firstname like :firstname");
        pagingQueryProvider.setSortKeys(sortKeys);

        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("xmlJdbcCustomItemReader")
                .dataSource(dataSource)
                .fetchSize(10)
                .rowMapper(new CustomerRowMapper())
                .queryProvider(pagingQueryProvider)
                .parameterValues(parameters)
                .build();
    }
}
