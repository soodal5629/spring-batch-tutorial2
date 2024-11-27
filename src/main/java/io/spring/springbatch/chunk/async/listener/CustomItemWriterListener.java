package io.spring.springbatch.chunk.async.listener;

import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;
import org.springframework.batch.core.ItemWriteListener;
import org.springframework.batch.item.Chunk;

@Slf4j
public class CustomItemWriterListener implements ItemWriteListener<Customer> {
    @Override
    public void beforeWrite(Chunk<? extends Customer> items) {
    }

    @Override
    public void afterWrite(Chunk<? extends Customer> items) {
        log.info("###### Thread : {}, Write Items Size : {}", Thread.currentThread().getName(), items.size());
    }

    @Override
    public void onWriteError(Exception exception, Chunk<? extends Customer> items) {
    }
}
