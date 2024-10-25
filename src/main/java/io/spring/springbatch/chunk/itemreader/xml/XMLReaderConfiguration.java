package io.spring.springbatch.chunk.itemreader.xml;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class XMLReaderConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job xmlReaderJob() {
        return new JobBuilder("xmlReaderJob", jobRepository)
                .start(xmlReaderStep())
                .build();
    }

    @Bean
    public Step xmlReaderStep() {
        return new StepBuilder("xmlReaderStep", jobRepository)
                .<XmlCustomer, XmlCustomer>chunk(3, transactionManager)
                .reader(xmlItemReader())
                .writer(xmlItemWriter())
                .build();
    }

    @Bean
    public ItemReader<? extends XmlCustomer> xmlItemReader() {
        return new StaxEventItemReaderBuilder<XmlCustomer>()
                .name("statXml")
                .resource(new ClassPathResource("/customer.xml"))
                // root fragment
                .addFragmentRootElements("customer")
                .unmarshaller(itemUnmarshaller())
                .build();
    }

    @Bean
    public Unmarshaller itemUnmarshaller() {
        Map<String, Class<?>> aliases = new HashMap<>();
        aliases.put("customer", XmlCustomer.class);
        aliases.put("id", Long.class);
        aliases.put("name", String.class);
        aliases.put("age", Integer.class);

        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.setAliases(aliases);

        return xStreamMarshaller;
    }

    @Bean
    public ItemWriter<XmlCustomer> xmlItemWriter() {
        return items -> {
            for(XmlCustomer item : items) {
                log.info(">>> item = {}", item);
            }
        };
    }
}
