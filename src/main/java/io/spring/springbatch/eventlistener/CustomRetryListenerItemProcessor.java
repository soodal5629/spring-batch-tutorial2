package io.spring.springbatch.eventlistener;

import org.springframework.batch.item.ItemProcessor;

public class CustomRetryListenerItemProcessor implements ItemProcessor<Integer, String> {
    int cnt = 0;

    @Override
    public String process(Integer item) throws Exception {
        if(cnt < 2) {
            if(cnt % 2 == 0) {
                cnt++;
            } else {
                cnt++;
                throw new CustomRetryException("failed process " + item);
            }
        }
        return String.valueOf(item);
    }
}
