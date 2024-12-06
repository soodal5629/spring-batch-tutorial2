package io.spring.springbatch.joblauncher.controller;

import io.spring.springbatch.joblauncher.JobInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.explore.JobExplorer;

import org.springframework.batch.core.launch.*;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@Slf4j
public class JobOperatorController {
    private final JobRegistry jobRegistry;
    private final JobExplorer jobExplorer;
    private final JobOperator jobOperator;

    @PostMapping("/batch/start")
    public String start(@RequestBody JobInfo jobInfo) throws NoSuchJobException, JobInstanceAlreadyExistsException, JobParametersInvalidException {
        Iterator<String> iterator = jobRegistry.getJobNames().iterator();
        while(iterator.hasNext()) {
            Job job = jobRegistry.getJob(iterator.next());
            if(job.getName().equals("jobOperatorJob")) {
                log.info("jobName = {}", job.getName());
                Properties properties = new Properties();
                properties.setProperty("id", jobInfo.getId());
                jobOperator.start(job.getName(), properties);
                break;
            }
        }
        return "batch is started";
    }

    @PostMapping("/batch/stop")
    public String stop() throws NoSuchJobException, NoSuchJobExecutionException, JobExecutionNotRunningException {
        Iterator<String> iterator = jobRegistry.getJobNames().iterator();
        while(iterator.hasNext()) {
            Job job = jobRegistry.getJob(iterator.next());
            if(job.getName().equals("jobOperatorJob")) {
                log.info("stopped jobName = {}", job.getName());
                Set<JobExecution> runningJobExecutions = jobExplorer.findRunningJobExecutions(job.getName());
                JobExecution jobExecution = runningJobExecutions.iterator().next();
                // 해당 메소드를 호출한다고 해서 바로 stop 되는 것이 아니라 실행중인 step 까지 실행 완료 후 stop
                jobOperator.stop(jobExecution.getId());
                break;
            }
        }
        return "batch is stopped";
    }

    @PostMapping("/batch/restart")
    public String restart() throws NoSuchJobException, NoSuchJobExecutionException, JobInstanceAlreadyCompleteException, JobParametersInvalidException, JobRestartException {
        Iterator<String> iterator = jobRegistry.getJobNames().iterator();
        while(iterator.hasNext()) {
            Job job = jobRegistry.getJob(iterator.next());
            if(job.getName().equals("jobOperatorJob")) {
                log.info("restarted jobName = {}", job.getName());
                JobInstance lastJobInstance = jobExplorer.getLastJobInstance(job.getName());
                JobExecution lastJobExecution = jobExplorer.getLastJobExecution(lastJobInstance);
                // job이 성공했을 경우에는 재시작이 안되므로 실패했을 경우에만 restart 되도록 해야 함
                jobOperator.restart(lastJobExecution.getId());
                break;
            }
        }
        return "batch is restarted";
    }
}
