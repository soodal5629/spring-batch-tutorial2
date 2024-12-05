package io.spring.springbatch.eventlistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class CustomRetryListenerItemWriter implements ItemWriter<String> {
    int cnt = 0;
    @Override
    public void write(Chunk<? extends String> chunk) throws Exception {
        for(String item : chunk) {
            if(cnt < 2) {
                if(cnt % 2 == 0) {
                    cnt++;
                } else {
                    cnt++;
                    throw new CustomRetryException("failed write " + item);
                }
            }
        }
        log.info("write items = {}", chunk);
    }
}
