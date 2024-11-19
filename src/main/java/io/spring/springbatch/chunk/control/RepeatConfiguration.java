package io.spring.springbatch.chunk.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class RepeatConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job repeatControlJob() {
        return new JobBuilder("repeatControlJob", jobRepository)
                .start(repeatControlStep())
                .build();
    }

    @Bean
    public Step repeatControlStep() {
        return new StepBuilder("repeatControlStep", jobRepository)
                .<String, String>chunk(5, transactionManager)
                .reader(new ItemReader<String>() {
                    int i = 0;
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        i++;
                        return i > 3 ? null : "item" + i;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    // RepeatTemplate 관련 코드
                    RepeatTemplate repeatTemplate = new RepeatTemplate();
                    @Override
                    public String process(String item) throws Exception {
                        // 하단의 iterate문 3번의 반복 후 종료 후 item 리턴
                        repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                log.info("repeatTemplate is testing");
                                return RepeatStatus.CONTINUABLE;
                            }
                        });
                        log.info("============== processed item - {}", item);
                        return item;
                    }
                })
                .writer(items -> log.info("written items = {}", items))
                .allowStartIfComplete(true)
                .build();
    }
}
