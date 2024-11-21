package io.spring.springbatch.chunk.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.HashMap;
import java.util.Map;

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
//                .skip(SkippableException.class)
//                .skipLimit(2)
                .skipPolicy(limitCheckingItemSkipPolicy())
                .noSkip(NoSkippableException.class) // 해당 예외가 발생하면 skip X
                .allowStartIfComplete(true)
                .build();
    }

    /**
     * 커스텀하게 SkipPolicy 설정 가능
     */
    @Bean
    public SkipPolicy limitCheckingItemSkipPolicy() {
        Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
        exceptionClass.put(SkippableException.class, true);
        LimitCheckingItemSkipPolicy skipPolicy = new LimitCheckingItemSkipPolicy(3, exceptionClass);

        return skipPolicy;
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
