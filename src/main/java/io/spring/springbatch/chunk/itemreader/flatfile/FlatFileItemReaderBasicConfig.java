package io.spring.springbatch.chunk.itemreader.flatfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;
import org.w3c.dom.ranges.RangeException;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFileItemReaderBasicConfig {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flatFileItemReaderBasicJob() {
        return new JobBuilder("flatFileItemReaderBasicJob", jobRepository)
                .start(flatFileItemReaderBasicStep())
                .build();
    }

    @Bean
    public Step flatFileItemReaderBasicStep() {
        return new StepBuilder("flatFileItemReaderBasicStep", jobRepository)
                .allowStartIfComplete(true)
                .<String, Customer>chunk(5, transactionManager)
                //.reader(flatFileItemReader())
                .reader(flatFileItemFixedLengthReader())
                .writer(new ItemWriter() {
                    @Override
                    public void write(Chunk chunk) throws Exception {
                        log.info("items = {}", chunk);
                    }
                })
                .build();
    }

    @Bean
    public ItemReader flatFileItemReader() {
//        FlatFileItemReader<Customer> itemReader = new FlatFileItemReader<>();
//        itemReader.setResource(new ClassPathResource("/customer.csv"));
//
//        DefaultLineMapper<Customer> lineMapper = new DefaultLineMapper<>();
//        lineMapper.setTokenizer(new DelimitedLineTokenizer(","));
//        lineMapper.setFieldSetMapper(new CustomerFieldSetMapper());
//
//        itemReader.setLineMapper(lineMapper);
//        itemReader.setLinesToSkip(1);

        // builder 클래스로 설정
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFile")
                .resource(new ClassPathResource("/customer.csv"))
                //.fieldSetMapper(new CustomerFieldSetMapper())
                // 스프링 배치에서 제공하는 구현체 사용 가능
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                // 스프링 배치에서 제공하는 구현체 사용 가능
                .delimited().delimiter(",")
                .names("name", "age", "year")
                .build();
    }

    // 고정 길이 기준으로 나누어서 읽음
    @Bean
    public ItemReader flatFileItemFixedLengthReader() {
        return new FlatFileItemReaderBuilder<Customer>()
                .name("flatFileItemFixedLengthReader")
                .resource(new FileSystemResource("C:\\Users\\chaer\\workspace\\spring-batch\\src\\main\\resources\\customer.txt"))
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>())
                .targetType(Customer.class)
                .linesToSkip(1)
                .fixedLength()
                .strict(false) // default: true
                .addColumns(new Range(1, 5))
                .addColumns(new Range(6, 9))
                .addColumns(new Range(10, 11))
//                .addColumns(new Range(1))
//                .addColumns(new Range(6))
//                .addColumns(new Range(10))
                .names("name", "year", "age")
                .build();
    }
}
