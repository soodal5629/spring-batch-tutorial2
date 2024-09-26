package io.spring.springbatch.jobrepository;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.DefaultJobKeyGenerator;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.database.support.DefaultDataFieldMaxValueIncrementerFactory;
import org.springframework.batch.support.DatabaseType;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
@RequiredArgsConstructor
public class CustomBatchConfigurer  {
    private final DataSource dataSource;
    private final PlatformTransactionManager transactionManager;

    @Bean
    public JobRepository customJobRepository() throws Exception {
        JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
        factory.setDataSource(dataSource);
        factory.setTransactionManager(transactionManager);
        factory.setIsolationLevelForCreate("ISOLATION_READ_COMMITTED"); // 트랜잭션 격리 수준
        factory.setTablePrefix("SYSTEM_");
        factory.setDatabaseType(DatabaseType.POSTGRES.name());
        // 해당 코드가 없으면 각종 설정을 직접 해줘야 함(에러 발생)
        factory.afterPropertiesSet();
        return factory.getObject();
    }

}
