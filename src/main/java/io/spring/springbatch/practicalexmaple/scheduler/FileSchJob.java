package io.spring.springbatch.practicalexmaple.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileSchJob extends QuartzJobBean {
    private final Job fileJob;
    private final JobLauncher jobLauncher;
    private final JobExplorer jobExplorer;

    @SneakyThrows
    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        String requestDate = (String) context.getJobDetail().getJobDataMap().get("requestDate");

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("id", new Date().getTime())
                .addString("requestDate", requestDate)
                .toJobParameters();

        // job instance 개수
        int fileJobInstanceCount = Long.valueOf(jobExplorer.getJobInstanceCount(fileJob.getName())).intValue();
        List<JobInstance> jobInstances = jobExplorer.getJobInstances(fileJob.getName(), 0, fileJobInstanceCount);
        if(jobInstances.size() > 0) {
            for (JobInstance jobInstance : jobInstances) {
                List<JobExecution> jobExecutions = jobExplorer.getJobExecutions(jobInstance);
                List<JobExecution> jobExecutionList = jobExecutions.stream().filter(jobExecution ->
                        jobExecution.getJobParameters().getString("requestDate").equals(requestDate)).collect(Collectors.toList());
                // 똑같은 파일 읽지 않도록 예외 발생 던짐
                if(jobExecutionList.size() > 0) {
                    throw new JobExecutionException(requestDate + " already exists");
                }
            }
        }


        jobLauncher.run(fileJob, jobParameters);
    }
}
