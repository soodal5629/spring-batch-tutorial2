package io.spring.springbatch.chunk.async.listener;

import io.spring.springbatch.chunk.itemreader.db.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.ItemProcessListener;

@Slf4j
public class CustomItemProcessorListener implements ItemProcessListener<Customer, Customer> {
    @Override
    public void beforeProcess(Customer item) {

    }

    @Override
    public void afterProcess(Customer item, Customer result) {
        log.info("###### Thread : {}, Process Item : {}", Thread.currentThread().getName(), item.getId());
    }

    @Override
    public void onProcessError(Customer item, Exception e) {
    }
}
