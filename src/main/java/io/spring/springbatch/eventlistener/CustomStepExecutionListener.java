package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.stereotype.Component;

// 빈으로 등록하면 스텝에서 동일한 데이터를 공유할 수 있음(각 Step의 ExecutionContext를 공유하는 것은 아님)
@Component
@Slf4j
public class CustomStepExecutionListener implements StepExecutionListener {
    @Override
    public void beforeStep(StepExecution stepExecution) {
        String stepName = stepExecution.getStepName();
        stepExecution.getExecutionContext().put("name", "abcd");
        log.info("interface 방식 리스너, stepName = {}", stepName);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        ExitStatus exitStatus = stepExecution.getExitStatus();
        BatchStatus status = stepExecution.getStatus(); // 배치 상태
        String name = (String) stepExecution.getExecutionContext().get("name");
        log.info("interface 방식 리스너, exitStatus = {}, status = {}, name = {}", exitStatus, status, name);
        
        return ExitStatus.COMPLETED;
    }
}
