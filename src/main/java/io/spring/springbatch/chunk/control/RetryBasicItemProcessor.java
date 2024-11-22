package io.spring.springbatch.chunk.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RetryBasicItemProcessor implements ItemProcessor<String, String> {
    private int cnt = 0;
    @Override
    public String process(String item) throws Exception {
        cnt++;
        log.info("item = {}, cnt = {}", item, cnt);
        throw new RetryableException("cnt = " + String.valueOf(cnt));
        //return "";
    }
}
