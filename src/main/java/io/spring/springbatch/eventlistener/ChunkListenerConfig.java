package io.spring.springbatch.eventlistener;

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
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChunkListenerConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    @Bean
    public Job chunkListenerJob() {
        return new JobBuilder("chunkListenerJob", jobRepository)
                .start(chunkListenerStep())
                .build();
    }

    @Bean
    public Step chunkListenerStep() {
        return new StepBuilder("chunkListenerStep", jobRepository)
                .<Integer, String>chunk(5, transactionManager)
                .allowStartIfComplete(true)
                .listener(new CustomChunkListener())
                .listener(new CustomItemReaderListener())
                .listener(new CustomItemProcessorListener())
                .listener(new CustomItemWriterListener())
                .reader(listItemReader())
                .processor((ItemProcessor<? super Integer, String>) item -> "item" + item)
                .writer((ItemWriter<? super String>) items -> {
                    //throw new RuntimeException("Failed");
                    log.info("items = {}", items);
                })
                .build();
    }

    @Bean
    public ItemReader<Integer> listItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
        return new ListItemReader<>(list);
    }
}
