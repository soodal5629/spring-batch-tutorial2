package io.spring.springbatch.joblauncher.controller;

import io.spring.springbatch.joblauncher.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.support.DefaultBatchConfiguration;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * HTTP 요청에 의해 비동기적으로 배치처리를 실행하기 위한 테스트 용도 컨트롤러
 * - HTTP 요청이 오면 클라이언트에게 실시간 응답을 줘야 하므로
 */
@RestController
@RequiredArgsConstructor
public class JobLauncherController {
    private final Job jobLauncherJob;
    private final JobLauncher jobLauncher;
    private final DefaultBatchConfiguration batchConfiguration;
    // 동기 방식
    @PostMapping("/batch")
    public String launch(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder().addString("id", member.getId())
                .addLocalDateTime("date", LocalDateTime.now()).toJobParameters();
        jobLauncher.run(jobLauncherJob, jobParameters);
        return "batch completed";
    }

    // 비동기 방식
    @PostMapping("/async-batch")
    public String asyncLaunch(@RequestBody Member member) throws JobInstanceAlreadyCompleteException, JobExecutionAlreadyRunningException, JobParametersInvalidException, JobRestartException {
        JobParameters jobParameters = new JobParametersBuilder().addString("id", member.getId())
                .addLocalDateTime("date", LocalDateTime.now()).toJobParameters();
        TaskExecutorJobLauncher asyncJobLauncher = (TaskExecutorJobLauncher) batchConfiguration.jobLauncher();
        // job을 비동기 처리할 수 있도록 설정
        asyncJobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
        asyncJobLauncher.run(jobLauncherJob, jobParameters);
        return "async batch completed";
    }
}
