package io.spring.springbatch.eventlistener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
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
public class SkipListenerConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job skipListenerJob() {
        return new JobBuilder("skipListenerJob", jobRepository)
                .start(skipListenerStep())
                .build();
    }

    @Bean
    public Step skipListenerStep() {
        return new StepBuilder("skipListenerStep", jobRepository)
                .<Integer, String>chunk(10, transactionManager)
                .allowStartIfComplete(true)
                .reader(skipItemReader())
                .processor(new ItemProcessor<Integer, String>() {
                    @Override
                    public String process(Integer item) throws Exception {
                        if(item == 4) {
                            throw new CustomSkipException("process skipped");
                        }
                        return "item" + item;
                    }
                })
                .writer(new ItemWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {
                        for (String item : chunk) {
                            if(item.equals("item5")) {
                                throw new CustomSkipException("write skipped");
                            }
                            log.info("write item = {}", item);
                        }
                    }
                })
                .faultTolerant()
                .skip(CustomSkipException.class)
                .skipLimit(3)
                .listener(new CustomSkipListener())
                .build();
    }

    @Bean
    public ItemReader<Integer> skipItemReader() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        return new LinkedListItemReader<>(list);
    }
}
