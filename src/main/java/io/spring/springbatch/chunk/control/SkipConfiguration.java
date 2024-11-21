package io.spring.springbatch.chunk.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class SkipConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job skipJob() {
        return new JobBuilder("skipJob", jobRepository)
                .start(skipStep())
                .build();
    }

    @Bean
    public Step skipStep() {
        return new StepBuilder("skipStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        if(i == 3) {
                            throw new SkippableException("skip");
                        }
                        return i > 20 ? null : String.valueOf(i);
                    }
                })
                .processor(sipItemProcessor())
                .writer(skipItemWriter())
                .faultTolerant()
                .skip(SkippableException.class)
                .skipLimit(2)
                .build();
    }

    @Bean
    public ItemWriter<? super String> skipItemWriter() {
        return new SkipItemWriter();
    }

    @Bean
    public ItemProcessor<? super String, String> sipItemProcessor() {
        return new SkipItemProcessor();
    }


}
