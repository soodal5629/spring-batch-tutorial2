package io.spring.springbatch.chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ChunkBasicConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job chunkBasicJob() {
        return new JobBuilder("chunkBasicJob", jobRepository)
                .start(chunkBasicStep1())
                .build();
    }

    @Bean
    public Step chunkBasicStep1() {
        return new StepBuilder("chunkBasicStep1", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new ListItemReader<>(Arrays.asList("item1", "item2", "item3", "item4", "item5")))
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        //Thread.sleep(300);
                        log.info("item = {}", item);
                        return "my " + item;
                    }
                })
                .writer(new ItemWriter<String>() {

                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        //Thread.sleep(300);
                        chunk.forEach(item -> log.info("chunk item = {}", item));
                    }
                }).allowStartIfComplete(true)
                .build();
    }



}
