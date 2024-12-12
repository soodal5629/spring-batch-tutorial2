package io.spring.springbatch.practicalexmaple.job.api;

import io.spring.springbatch.practicalexmaple.chunck.processor.ApiItemProcessor1;
import io.spring.springbatch.practicalexmaple.chunck.processor.ApiItemProcessor2;
import io.spring.springbatch.practicalexmaple.chunck.processor.ApiItemProcessor3;
import io.spring.springbatch.practicalexmaple.chunck.writer.ApiItemWriter1;
import io.spring.springbatch.practicalexmaple.chunck.writer.ApiItemWriter2;
import io.spring.springbatch.practicalexmaple.chunck.writer.ApiItemWriter3;
import io.spring.springbatch.practicalexmaple.classifier.ProcessorClassifier;
import io.spring.springbatch.practicalexmaple.classifier.WriterClassifier;
import io.spring.springbatch.practicalexmaple.domain.ApiRequestVO;
import io.spring.springbatch.practicalexmaple.domain.Product;
import io.spring.springbatch.practicalexmaple.domain.ProductVO;
import io.spring.springbatch.practicalexmaple.partition.ProductPartitioner;
import io.spring.springbatch.practicalexmaple.service.ApiService1;
import io.spring.springbatch.practicalexmaple.service.ApiService2;
import io.spring.springbatch.practicalexmaple.service.ApiService3;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.PostgresPagingQueryProvider;
import org.springframework.batch.item.support.ClassifierCompositeItemProcessor;
import org.springframework.batch.item.support.ClassifierCompositeItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class ApiStepConfiguration {
    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;
    private final DataSource dataSource;
    private final ApiService1 apiService1;
    private final ApiService2 apiService2;
    private final ApiService3 apiService3;
    private int chunkSize = 10;

    @Bean
    public Step apiMasterStep() throws Exception {
        return new StepBuilder("apiMasterStep", jobRepository)
                .partitioner(apiSlaveStep().getName(), partitioner(dataSource))
                .step(apiSlaveStep())
                .gridSize(3)
                .taskExecutor(taskExecutor())
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(3);
        taskExecutor.setMaxPoolSize(6);
        taskExecutor.setThreadNamePrefix("api-thread-");

        return taskExecutor;
    }

    @Bean
    public Step apiSlaveStep() throws Exception {
        return new StepBuilder("apiSlaveStep", jobRepository)
                .allowStartIfComplete(true)
                .<ProductVO, ProductVO>chunk(chunkSize, transactionManager)
                .reader(apiItemReader(null))
                .processor(apiItemProcessor())
                .writer(apiItemWriter())
                .build();
    }

    @Bean
    public ProductPartitioner partitioner(DataSource dataSource) {
        ProductPartitioner productPartitioner = new ProductPartitioner(dataSource);
        return productPartitioner;
    }

    @Bean
    @StepScope
    /**
     * @StepScope, @Value("#{stepExecutionContext['product']}") 를 이용하여 DTO/VO 객체를 읽으려면 해당 클래스는 Serializable 을 구현해야 함
     * 안그러면 IllegalArgumentException 발생
     */
    public ItemReader<ProductVO> apiItemReader(@Value("#{stepExecutionContext['product']}") ProductVO productVO) throws Exception {
        JdbcPagingItemReader<ProductVO> reader = new JdbcPagingItemReader<>();
        reader.setDataSource(dataSource);
        reader.setPageSize(chunkSize);
        reader.setRowMapper(new BeanPropertyRowMapper<>(ProductVO.class));

        PostgresPagingQueryProvider queryProvider = new PostgresPagingQueryProvider();
        queryProvider.setSelectClause("id, name, price, type");
        queryProvider.setFromClause("from product");
        queryProvider.setWhereClause("where type = :type");

        HashMap<String, Order> sortKeys = new HashMap<>(1);
        sortKeys.put("id", Order.DESCENDING);
        queryProvider.setSortKeys(sortKeys);

        reader.setParameterValues(QueryGenerator.getParameterForQuery("type", productVO.getType()));
        reader.setQueryProvider(queryProvider);
        reader.afterPropertiesSet();

        return reader;
    }

    /**
     * 조건에 맞는 ItemProcessor 호출
     */
    @Bean
    public ItemProcessor apiItemProcessor() {
        ClassifierCompositeItemProcessor<ProductVO, ApiRequestVO> processor = new ClassifierCompositeItemProcessor<>();
        ProcessorClassifier<ProductVO, ItemProcessor<?, ? extends ApiRequestVO>> classifier = new ProcessorClassifier<>();
        Map<String, ItemProcessor<ProductVO, ApiRequestVO>> map = new HashMap<>();
        map.put("1", new ApiItemProcessor1());
        map.put("2", new ApiItemProcessor2());
        map.put("3", new ApiItemProcessor3() );
        classifier.setProcessorMap(map);
        processor.setClassifier(classifier);

        return processor;
    }

    /**
     * 조건에 맞는 ItemWriter 호출
     */
    @Bean
    public ItemWriter apiItemWriter() {
        ClassifierCompositeItemWriter<ApiRequestVO> writer = new ClassifierCompositeItemWriter<>();
        WriterClassifier<ApiRequestVO, ItemWriter<? super ApiRequestVO>> classifier = new WriterClassifier<>();
        Map<String, ItemWriter<ApiRequestVO>> map = new HashMap<>();
        map.put("1", new ApiItemWriter1(apiService1));
        map.put("2", new ApiItemWriter2(apiService2));
        map.put("3", new ApiItemWriter3(apiService3));
        classifier.setWriterMap(map);
        writer.setClassifier(classifier);

        return writer;
    }
}
