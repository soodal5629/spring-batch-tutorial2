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
import org.springframework.batch.item.adapter.ItemReaderAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * 배치 작업에서 기존의 DAO나 Service를 이용하고자 할 때 ItemReaderAdapter 사용
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemReaderAdapterConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job itemReaderAdapterJob() {
        return new JobBuilder("itemReaderAdapterJob", jobRepository)
                .start(itemReaderAdapterStep())
                .build();
    }

    @Bean
    public Step itemReaderAdapterStep() {
        return new StepBuilder("itemReaderAdapterStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(itemReaderAdapterCustomReader())
                .writer(itemReaderAdapterCustomWriter())
                .build();
    }

    @Bean
    public ItemReader<String> itemReaderAdapterCustomReader() {
        ItemReaderAdapter<String> reader = new ItemReaderAdapter<>();
        reader.setTargetObject(customService());
        reader.setTargetMethod("customRead");

        return reader;
    }

    @Bean
    public Object customService() {
        return new CustomService();
    }

    @Bean
    public ItemWriter<String> itemReaderAdapterCustomWriter() {
        return items -> {
            for (String item : items) {
                log.info("item = {}", item);
            }
            log.info("==================");
        };
    }
}
