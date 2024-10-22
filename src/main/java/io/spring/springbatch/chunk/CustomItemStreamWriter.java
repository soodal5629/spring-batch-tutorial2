package io.spring.springbatch.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemStreamException;
import org.springframework.batch.item.ItemStreamWriter;

@Slf4j
public class CustomItemStreamWriter implements ItemStreamWriter<String> {
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        chunk.forEach(item -> log.info("CustomItemStreamWriter write item = {}", item));
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter open");
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        log.info("CustomItemStreamWriter update");
    }

    @Override
    public void close() throws ItemStreamException {
        log.info("CustomItemStreamWriter close");
    }
}
