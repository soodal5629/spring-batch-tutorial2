package io.spring.springbatch.chunk.itemWriter.itemwriteradapter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomItemWriterAdapterService<T> {
    public void customItemWriterAdapter(T item) {
        log.info("writer adapter item = {}", item);
    }
}
