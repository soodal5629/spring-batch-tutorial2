package io.spring.springbatch.chunk.itemWriter.db;

import io.spring.springbatch.chunk.itemWriter.db.jpa.CustomJpaItemWriterProcessor;
import io.spring.springbatch.chunk.itemWriter.db.jpa.Customer2;
import io.spring.springbatch.chunk.itemWriter.xml.CustomerRowMapper;
import io.spring.springbatch.chunk.itemreader.db.Customer;
import jakarta.persistence.EntityManagerFactory;
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
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
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
public class JpaItemWriterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final EntityManagerFactory entityManagerFactory;
    private final DataSource dataSource;

    @Bean
    public Job jpaItemWriterJob() {
        return new JobBuilder("jpaItemWriterJob", jobRepository)
                .start(jpaItemWriterStep())
                .build();
    }

    @Bean
    public Step jpaItemWriterStep() {
        return new StepBuilder("jpaItemWriterStep", jobRepository)
                .<Customer, Customer2>chunk(10, transactionManager)
                .reader(dbJpaCustomItemReader())
                .processor(customJpaItemProcessor())
                .writer(customJpaItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer2> customJpaItemWriter() {
        return new JpaItemWriterBuilder<Customer2>()
                .usePersist(true) // default: true 이기 때문에 true 설정할 거라면 해당 설정 생략해도 됨
                .entityManagerFactory(entityManagerFactory)
                .build();
    }

    @Bean
    public ItemProcessor<? super Customer, ? extends Customer2> customJpaItemProcessor() {
        return new CustomJpaItemWriterProcessor();
    }

    @Bean
    public ItemReader<Customer> dbJpaCustomItemReader() {
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
