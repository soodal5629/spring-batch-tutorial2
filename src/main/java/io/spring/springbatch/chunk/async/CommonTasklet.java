package io.spring.springbatch.chunk.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

@Slf4j
public class CommonTasklet implements Tasklet {
    private long sum;
    // 동시성 문제 방지 방법 1
    private Object lock = new Object();
    // 동시성 문제 방지 방법 2: sum 변수를 execute 내에 지역변수로 선언
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        // 동기화가 안되어 있음 -> 따라서 쓰기 작업할 때 여러 스레드가 멤버 변수에 동시에 접근할 수 있으므로 sum 값이 이상해질 수 있음
        // -> 동시성 이슈 발생 가능성 존재
        synchronized (lock) {
            for(int i = 0; i < 10000; i++) {
                sum++;
            }
            log.info("##### Thread = {}, Step = {}, sum = {}", Thread.currentThread().getName()
                    , chunkContext.getStepContext().getStepName(), sum);
        }

        return RepeatStatus.FINISHED;
    }
}
