package io.spring.springbatch.chunk.itemWriter.flatfile;

import io.spring.springbatch.chunk.itemreader.Customer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.transaction.PlatformTransactionManager;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class FlatFilesDelimitedConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public Job flatFileDelimitedJob() {
        return new JobBuilder("flatFileDelimitedJob", jobRepository)
                .start(flatFileDelimitedStep())
                .build();
    }

    @Bean
    public Step flatFileDelimitedStep() {
        return new StepBuilder("flatFileDelimitedStep", jobRepository)
                .<Customer, Customer>chunk(10, transactionManager)
                .reader(flatFileDelimitedItemReader())
                .writer(flatFileDelimitedItemWriter())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public ItemWriter<? super Customer> flatFileDelimitedItemWriter() {
        return new FlatFileItemWriterBuilder<Customer>()
                .name("flatFileItemWriter")
                // 해당 파일에 write
                .resource(new FileSystemResource("C:\\Users\\chaer\\workspace\\spring-batch\\src\\main\\resources\\flat-delimited-customer.txt"))
                .append(true) // default: false(기존 데이터 초기화하고 새로 씀)
                .shouldDeleteIfEmpty(true) // default: false, true이면 write할 데이터가 없을 경우 파일 삭제함
                .delimited()
                .delimiter("|")
                .names(new String[] {"id", "name", "age"})
                //.names("id", "name", "age")
                .build();
    }

    @Bean
    public ItemReader<? extends Customer> flatFileDelimitedItemReader() {
        List<Customer> customers = Arrays.asList(
                new Customer(1L, "hong gil dong1", 41)
                , new Customer(2L, "hong gil dong2", 44)
                , new Customer(3L, "hong gil dong3", 45));
        ListItemReader<Customer> reader = new ListItemReader<>(customers);
        return reader;
    }
}

