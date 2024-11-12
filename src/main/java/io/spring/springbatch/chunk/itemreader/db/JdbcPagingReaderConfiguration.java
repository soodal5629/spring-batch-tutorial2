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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.PagingQueryProvider;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.SqlPagingQueryProviderFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JdbcPagingReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job jdbcPagingReaderJob() throws Exception {
        return new JobBuilder("jdbcPagingReaderJob", jobRepository)
                .start(jdbcPagingReaderStep())
                .build();
    }

    @Bean
    public Step jdbcPagingReaderStep() throws Exception {
        return new StepBuilder("jdbcPagingReaderStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jdbcPagingReader())
                .writer(jdbcPagingWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> jdbcPagingReader() throws Exception {
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("firstname", "R%");
        return new JdbcPagingItemReaderBuilder<Customer>()
                .name("jdbcPagingReader")
                .pageSize(10)
                .dataSource(dataSource)
                .rowMapper(new BeanPropertyRowMapper<>(Customer.class))
                .queryProvider(createQueryProvider())
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public PagingQueryProvider createQueryProvider() throws Exception {
        SqlPagingQueryProviderFactoryBean queryProvider = new SqlPagingQueryProviderFactoryBean();
        queryProvider.setDataSource(dataSource);
        queryProvider.setSelectClause("id, firstname, lastname, birthdate");
        queryProvider.setFromClause("from customer");
        queryProvider.setWhereClause("where firstname like :firstname");

        Map<String, Order> sortKeys = new HashMap<>();
        sortKeys.put("id", Order.ASCENDING);

        queryProvider.setSortKeys(sortKeys);

        return queryProvider.getObject();
    }

    @Bean
    public ItemWriter<Customer> jdbcPagingWriter() {
        return items -> {
            for(Customer item : items) {
                log.info("jdbc paging item = {}", item.toString());
            }
            log.info("==========================");
        };
    }
}
