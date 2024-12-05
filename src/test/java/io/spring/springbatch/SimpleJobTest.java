package io.spring.springbatch;

import io.spring.springbatch.chunk.itemreader.db.JdbcPagingReaderConfiguration;
import org.aspectj.lang.annotation.After;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.*;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBatchTest
@SpringBootTest(classes = { JdbcPagingReaderConfiguration.class, TestBatchConfig.class })
public class SimpleJobTest {
    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void test() throws Exception {
        // given
        JobParameters jobParameters = new JobParametersBuilder()
                .addString("name", "test")
                .addLong("date", new Date().getTime())
                .toJobParameters();
        // when
        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);
        // step도 개별 실행 가능
        JobExecution jobExecution2 = jobLauncherTestUtils.launchStep("jdbcPagingReaderStep");
        StepExecution stepExecution = (StepExecution) ((List) jobExecution2.getStepExecutions()).get(0);

        // then
        assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(jobExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);


        assertThat(stepExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
        assertThat(stepExecution.getExitStatus()).isEqualTo(ExitStatus.COMPLETED);
        assertThat(stepExecution.getReadCount()).isEqualTo(12);
    }
}
