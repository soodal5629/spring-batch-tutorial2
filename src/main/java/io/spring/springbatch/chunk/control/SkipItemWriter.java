package io.spring.springbatch.chunk.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class SkipItemWriter implements ItemWriter<String> {
    private int cnt = 0;

    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        cnt++;
        for (String item : chunk) {
            if(item.equals("-12")) {
                throw new SkippableException("Write failed cnt : " + cnt);
            }
            log.info("ItemWriter item : {}", item);
        }
        log.info("================");
    }
}
