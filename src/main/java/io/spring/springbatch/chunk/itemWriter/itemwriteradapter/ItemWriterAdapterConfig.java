package io.spring.springbatch.chunk.itemWriter.itemwriteradapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ItemWriterAdapterConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job itemWriterAdapterJob() {
        return new JobBuilder("itemWriterAdapterJob", jobRepository)
                .start(itemWriterAdapterStep())
                .build();
    }

    @Bean
    public Step itemWriterAdapterStep() {
        return new StepBuilder("itemWriterAdapterStep", jobRepository)
                .<String, String>chunk(10, transactionManager)
                .reader(new ItemReader<>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 10 ? null : "item" + i; // 무한 실행 방지
                    }

                })
                .writer(customItemWriterAdapter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemWriter<? super String> customItemWriterAdapter() {
        ItemWriterAdapter<String> adapterWriter = new ItemWriterAdapter<>();
        adapterWriter.setTargetObject(customItemWriterAdapterService());
        adapterWriter.setTargetMethod("customItemWriterAdapter");

        return adapterWriter;
    }

    @Bean
    public CustomItemWriterAdapterService customItemWriterAdapterService() {
        return new CustomItemWriterAdapterService();
    }
}
