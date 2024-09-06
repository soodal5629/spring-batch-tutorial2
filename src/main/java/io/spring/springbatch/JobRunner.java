package io.spring.springbatch;

import lombok.RequiredArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * ApplicationRunner: 스프링 부트 초기화 및 완료가 끝나면 가장 먼저 호출하는 타입의 클래스
 * 스프링 부트에서도 스프링 배치를 초기화하면서 내부적으로 JobLauncher를 가지고 job을 실행시키는 단계가 있음
 * 해당 클래스는 수동으로 job을 실행시킬 수 있도록 위의 단계를 비슷하게 만든 것임
 */
@Component
@RequiredArgsConstructor
public class JobRunner implements ApplicationRunner {

    private final JobLauncher jobLauncher;
    private final Job jobInstanceJob;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        JobParameters jobParameters = new JobParametersBuilder().addString("name", "user2")
                .toJobParameters();
        jobLauncher.run(jobInstanceJob, jobParameters);
    }

}
