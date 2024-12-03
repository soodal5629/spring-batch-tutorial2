package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReaderListener implements ItemReadListener {
    @Override
    public void beforeRead() {
        log.info(">> before Read");
    }

    @Override
    public void afterRead(Object item) {
        log.info(">> after Read = {}", item);
    }

    @Override
    public void onReadError(Exception ex) {
        log.info(">> onReadError ", ex);
    }
}
