package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class CustomItemWriterListener implements ItemWriteListener<String> {
    @Override
    public void beforeWrite(Chunk<? extends String> items) {
        log.info(">> before Write");
    }

    @Override
    public void afterWrite(Chunk<? extends String> items) {
        log.info(">> after Write");
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends String> items) {
        log.info(">> onWriteError ", exception);
    }
}
