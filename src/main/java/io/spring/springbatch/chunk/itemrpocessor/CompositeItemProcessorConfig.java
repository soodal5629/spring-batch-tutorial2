package io.spring.springbatch.chunk.itemrpocessor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class CompositeItemProcessorConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job compositionItemProcessorJob() {
        return new JobBuilder("compositionItemProcessorJob", jobRepository)
                .start(compositionItemProcessorStep())
                .build();
    }

    @Bean
    public Step compositionItemProcessorStep() {
        return new StepBuilder("compositionItemProcessorStep", jobRepository)
                .allowStartIfComplete(true)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item";
                    }
                })
                .processor(compositeItemProcessor())
                .writer(items -> log.info("composition processor item = {}", items))
                .build();
    }

    @Bean
    public ItemProcessor<? super String, String> compositeItemProcessor() {
        List itemProcessors = new ArrayList();
        itemProcessors.add(new CustomItemProcessor());
        itemProcessors.add(new CustomItemProcessor2());

        return new CompositeItemProcessorBuilder<String, String>()
                .delegates(itemProcessors)
                .build();
    }
}
