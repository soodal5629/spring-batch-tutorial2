package io.spring.springbatch.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomItemWriter implements ItemWriter<Customer> {
    @Override
    public void write(Chunk<? extends Customer> chunk) throws Exception {
        chunk.forEach(item -> log.info("customer = {}", item));
    }
}
