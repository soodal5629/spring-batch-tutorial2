package io.spring.springbatch.chunk.itemWriter.xml;

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
import org.springframework.batch.item.xml.builder.StaxEventItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class XMLWriterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;

    @Bean
    public Job xmlWriterJob() {
        return new JobBuilder("xmlWriterJob", jobRepository)
                .start(xmlWriterStep())
                .build();
    }

    @Bean
    public Step xmlWriterStep() {
        return new StepBuilder("xmlWriterStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(xmlJdbcCustomItemReader())
                .writer(xmlCustomItemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemReader<Customer> xmlJdbcCustomItemReader() {
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
//                .selectClause("id, firstname, lastname, birthdate")
//                .fromClause("from customer")
//                .whereClause("where firstname like :firstname")
                .queryProvider(pagingQueryProvider)
                //.queryProvider(new PostgresPagingQueryProvider())
                //.sortKeys(sortKeys)
                .parameterValues(parameters)
                .build();
    }

    @Bean
    public ItemWriter<Customer> xmlCustomItemWriter() {
        return new StaxEventItemWriterBuilder<Customer>()
                .name("staxEventWriter")
                .marshaller(itemMarshaller())
                .resource(new FileSystemResource("C:\\Users\\chaer\\workspace\\spring-batch\\src\\main\\resources\\customer-xmlread.xml"))
                .rootTagName("customer")
                .overwriteOutput(true)
                .build();
    }

    @Bean
    public Marshaller itemMarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", Customer.class);
        aliases.put("id", Long.class);
        aliases.put("firstName", String.class);
        aliases.put("lastName", String.class);
        aliases.put("birthdate", Date.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);

        return xStreamMarshaller;
    }
}
