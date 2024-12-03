package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class CustomItemProcessorListener implements ItemProcessListener<Integer, String> {
    @Override
    public void beforeProcess(Integer item) {
        log.info(">> before Process");
    }

    @Override
    public void afterProcess(Integer item, String result) {
        log.info(">> after Process = {}", item);
    }

    @Override
    public void onProcessError(Integer item, Exception e) {
        log.info(">> on Process Error ", e);
    }
}
