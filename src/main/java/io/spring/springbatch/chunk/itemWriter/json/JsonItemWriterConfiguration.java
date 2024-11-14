package io.spring.springbatch.chunk.itemWriter.json;

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
import org.springframework.batch.item.database.builder.JdbcPagingItemReaderBuilder;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.json.JacksonJsonObjectMarshaller;
import org.springframework.batch.item.json.builder.JsonFileItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class JsonItemWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job jsonItemWriterJob() {
        return new JobBuilder("jsonItemWriterJob", jobRepository)
                .start(jsonItemWriterStep())
                .build();
    }

    @Bean
    public Step jsonItemWriterStep() {
        return new StepBuilder("jsonItemWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(jsonJdbcCustomItemReader())
                .writer(jsonCustomItemWriter())
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> jsonCustomItemWriter() {
        return new JsonFileItemWriterBuilder<Customer>()
                .name("jsonCustomItemWriter")
                .jsonObjectMarshaller(new JacksonJsonObjectMarshaller<>())
                .resource(new FileSystemResource("C:\\Users\\chaer\\workspace\\spring-batch\\src\\main\\resources\\customer-read.json"))
                .build();
    }

    @Bean
    public ItemReader<Customer> jsonJdbcCustomItemReader() {
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
