package io.spring.springbatch.chunk.itemrpocessor;

import io.spring.springbatch.chunk.CustomItemProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.classify.Classifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ClassifierCompositeItemProcessorConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job classifierCompositeItemProcessorJob() {
        return new JobBuilder("classifierCompositeItemProcessorJob", jobRepository)
                .start(classifierCompositeItemProcessorStep())
                .build();
    }

    @Bean
    public Step classifierCompositeItemProcessorStep() {
        return new StepBuilder("classifierCompositeItemProcessorStep", jobRepository)
                .<ProcessorInfo, ProcessorInfo>chunk(10, transactionManager)
                .reader(new ItemReader<>() {
                    int i = 0;
                    @Override
                    public ProcessorInfo read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        ProcessorInfo processorInfo = ProcessorInfo.builder().id(i).build();
                        return i > 3 ? null : processorInfo;
                    }
                })
                .processor(classifierCompositeItemProcessor())
                .writer(items -> log.info("classifier composite processor item = {}", items))
                .build();
    }

    @Bean
    public ItemProcessor<? super ProcessorInfo, ? extends ProcessorInfo> classifierCompositeItemProcessor() {
        ClassifierCompositeItemProcessor<ProcessorInfo, ProcessorInfo> processor = new ClassifierCompositeItemProcessor<>();
        ProcessorClassifier<ProcessorInfo, ItemProcessor<?, ? extends ProcessorInfo>> classifier =
                new ProcessorClassifier<>();
        Map<Integer, ItemProcessor<ProcessorInfo, ProcessorInfo>> processorMap = new HashMap<>();

        processorMap.put(1, new ClassifierCustomItemProcessor1());
        processorMap.put(2, new ClassifierCustomItemProcessor2());
        processorMap.put(3, new ClassifierCustomItemProcessor3());

        classifier.setProcessorMap(processorMap);

        processor.setClassifier(classifier);

        return processor;
    }

}
