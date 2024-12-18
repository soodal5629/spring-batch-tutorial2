package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.SkipListener;
@Slf4j
public class CustomSkipListener implements SkipListener<Integer, String> {
    @Override
    public void onSkipInRead(Throwable t) {
        log.info(">> onSkippedRead : {}", t.getMessage());
    }

    @Override
    public void onSkipInWrite(String item, Throwable t) {
        log.info(">> onSkipInWrite item : {} ", item, t.getMessage());
    }

    @Override
    public void onSkipInProcess(Integer item, Throwable t) {
        log.info(">> onSkipInProcess item : {} ", item, t.getMessage());
    }
}
