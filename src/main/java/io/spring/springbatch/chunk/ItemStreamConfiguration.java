package io.spring.springbatch.chunk;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemStreamConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job itemStreamJob() {
        return new JobBuilder("itemStreamJob", jobRepository)
                .start(itemStreamStep())
                .build();
    }

    @Bean
    public Step itemStreamStep() {
        return new StepBuilder("itemStreamStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(customItemStreamReader())
                .writer(customItemStreamWriter())
                .build();
    }

    public CustomItemStreamReader customItemStreamReader() {
        List<String> items = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            items.add(String.valueOf(i));
        }
        return new CustomItemStreamReader(items);
    }

    @Bean
    public ItemWriter<? super String> customItemStreamWriter() {
        return new CustomItemStreamWriter();
    }
}
