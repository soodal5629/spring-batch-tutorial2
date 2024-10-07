package io.spring.springbatch.step;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.*;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * Step 개요 Config 클래스 (여러 api에 따라 생성되는 빌더 클래스 확인)
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class StepOutlineConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job stepOutlineJob() {
        return new JobBuilder("stepOutlineJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(stepOutline1())
                .next(stepOutline2())
                .next(stepOutline3())
                .build();
    }

    @Bean
    public Step stepOutline1() {
        return new StepBuilder("stepOutline1", jobRepository)
                .tasklet((contribution, chunkContext) -> {
                    log.info("stepOutline1 has executed");
                    return RepeatStatus.FINISHED;
                }, transactionManager)
                .build();
    }

    /**
     * 청크 기반 작업 처리하는 step -> reader, processor, writer
     */
    @Bean
    public Step stepOutline2() {
        return new StepBuilder("stepOutline2", jobRepository)
                .<String, String>chunk(3, transactionManager)
                .reader(new ItemReader<String>() {
                    @Override
                    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
                        return null;
                    }
                })
                .processor(new ItemProcessor<String, String>() {
                    @Override
                    public String process(String item) throws Exception {
                        return null;
                    }
                })
                .writer(new ItemStreamWriter<String>() {
                    @Override
                    public void write(Chunk<? extends String> chunk) throws Exception {

                    }
                })
                .build();
    }

    /**
     * partitioner 사용(멀티스레드 작업)
     */
    @Bean
    public Step stepOutline3() {
        return new StepBuilder("stepOutline3", jobRepository)
                .partitioner(stepOutline1())
                .gridSize(2)
                .build();
    }

    /**
     * Step 내부에서 Job 실행
     */
    @Bean
    public Step stepOutline4() {
        return new StepBuilder("stepOutline4", jobRepository)
                .job(stepJob())
                .build();
    }

    /**
     * Step 내부에서 Flow 실행
     */
    @Bean
    public Step stepOutline5() {
        return new StepBuilder("stepOutline5", jobRepository)
                .flow(stepFlow())
                .build();
    }

    @Bean
    public Job stepJob() {
        return new JobBuilder("stepJob", jobRepository)
                .start(stepOutline1())
                .next(stepOutline2())
                .next(stepOutline3())
                .build();
    }

    @Bean
    public Flow stepFlow() {
        FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("stepFlow");
        flowBuilder.start(stepOutline2()).end();
        return flowBuilder.build();
    }
}
