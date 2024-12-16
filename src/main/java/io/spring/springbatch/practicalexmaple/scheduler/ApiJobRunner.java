package io.spring.springbatch.practicalexmaple.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static org.quartz.JobBuilder.newJob;

@Component
@RequiredArgsConstructor
@Slf4j
public class ApiJobRunner extends PracticalJobRunner {
    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) {
        // JobDetail: job에 대한 여러 정보를 담고 있음
        JobDetail jobDetail = buildJobDetail(ApiSchJob.class, "apiJob", "batch", new HashMap());
        // 스케쥴러 job을 실행할 트리거 정보 - 30초마다 실행
        Trigger trigger = buildJobTrigger("0/30 * * * * ?");
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch(SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
