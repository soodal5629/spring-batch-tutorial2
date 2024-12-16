package io.spring.springbatch.practicalexmaple.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.boot.ApplicationArguments;
import org.springframework.stereotype.Component;

import java.util.HashMap;


@Component
@RequiredArgsConstructor
@Slf4j
public class FileJobRunner extends PracticalJobRunner {
    private final Scheduler scheduler;

    @Override
    protected void doRun(ApplicationArguments args) {
        String[] sourceArgs = args.getSourceArgs();

        // JobDetail: job에 대한 여러 정보를 담고 있음
        JobDetail jobDetail = buildJobDetail(FileSchJob.class, "fileJob", "batch", new HashMap());
        // 스케쥴러 job을 실행할 트리거 정보 - 50초마다 실행
        Trigger trigger = buildJobTrigger("0/50 * * * * ?");
        jobDetail.getJobDataMap().put("requestDate", sourceArgs[0]);
        try {
            scheduler.scheduleJob(jobDetail, trigger);
        } catch(SchedulerException e) {
            log.error(e.getMessage());
        }
    }
}
