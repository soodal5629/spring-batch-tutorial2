package io.spring.springbatch.chunk.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class SkipItemProcessor implements ItemProcessor<String, String> {
    private int cnt = 0;

    @Override
    public String process(String item) throws Exception {
        cnt++;
        if(item.equals("6") || item.equals("7")) {
            log.info("!!! process exception item - {}", item);
            throw new SkippableException("Process failed cnt : " + cnt);
        }
        log.info("ItemProcessor : {}", item);
        return String.valueOf(Integer.valueOf(item) * -1);
    }
}
