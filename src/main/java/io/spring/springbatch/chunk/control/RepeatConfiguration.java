package io.spring.springbatch.chunk.control;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.concurrent.TimeUnit;

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
                        //repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
                        // 하단의 iterate문 3초 동안 반복 후 종료 후 item 리턴
                        //repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(1000));

                        // 여러개의 CompletionPolicy를 적용할 수 있음 - 먼저 충족되는 CompletionPolicy 가 있으면 반복문 종료
                        CompositeCompletionPolicy compositeCompletionPolicy = new CompositeCompletionPolicy();
                        CompletionPolicy[] completionPolicies = new CompletionPolicy[] {
                                                                    new SimpleCompletionPolicy(3)
                                                                    , new TimeoutTerminationPolicy(3000)};
                        compositeCompletionPolicy.setPolicies(completionPolicies);
                        //repeatTemplate.setCompletionPolicy(compositeCompletionPolicy);
                        // 예외가 4번 이상 발생하면 반복문 종료
                        //repeatTemplate.setExceptionHandler(new SimpleLimitExceptionHandler(3));
                        repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler());
                        repeatTemplate.iterate(new RepeatCallback() {
                            @Override
                            public RepeatStatus doInIteration(RepeatContext context) throws Exception {
                                log.info("repeatTemplate is testing");
                                throw new RuntimeException("Exception is occurred!!");
                                //return RepeatStatus.CONTINUABLE;
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

    @Bean
    public ExceptionHandler simpleLimitExceptionHandler() {
        return new SimpleLimitExceptionHandler(3);
    }
}
