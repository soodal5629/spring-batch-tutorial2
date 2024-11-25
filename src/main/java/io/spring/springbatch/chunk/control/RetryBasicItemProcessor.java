package io.spring.springbatch.chunk.control;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class RetryBasicItemProcessor implements ItemProcessor<String, String> {
    private int cnt = 0;
    @Override
    public String process(String item) throws Exception {
        // 재시도해도 item은 에러가 발생했던 것을 skip 하지 않고 다시 처음부터 재시도하여 매번 예외가 동일하게 발생한다(item이 skip되지 않음)
        // 따라서 특정 아이템을 건너뛰고 retry 하고 싶다면 skip 설정을 하면 됨
        if(item.equals("2") || item.equals("3")) {
            cnt++;
            log.info("item = {}, cnt = {}", item, cnt);
            throw new RetryableException("Failed cnt = " + cnt);
        }
        log.info("### processed item = {}", item);
        return item;
    }
}
