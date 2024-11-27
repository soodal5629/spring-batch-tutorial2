package io.spring.springbatch.chunk.async.listener;

import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemReadListener;

@Slf4j
public class CustomItemReadListener implements ItemReadListener<Customer> {
    @Override
    public void beforeRead() {
    }

    @Override
    public void afterRead(Customer item) {
        log.info("###### Thread : {}, Read Item : {}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onReadError(Exception ex) {
    }
}
