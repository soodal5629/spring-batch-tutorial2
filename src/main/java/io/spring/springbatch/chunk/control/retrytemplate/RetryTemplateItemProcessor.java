package io.spring.springbatch.chunk.control.retrytemplate;

import io.spring.springbatch.chunk.control.RetryableException;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.classify.BinaryExceptionClassifier;
import org.springframework.classify.Classifier;
import org.springframework.retry.RecoveryCallback;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.DefaultRetryState;
import org.springframework.retry.support.RetryTemplate;

public class RetryTemplateItemProcessor implements ItemProcessor<String, Item> {
    private RetryTemplate customRetryTemplate;

    public RetryTemplateItemProcessor(RetryTemplate customRetryTemplate) {
        this.customRetryTemplate = customRetryTemplate;
    }

    private int cnt;

    @Override
    public Item process(String item) throws Exception {
        Classifier<Throwable, Boolean> rollbackClassifier = new BinaryExceptionClassifier(true);

        Item returnItem = customRetryTemplate.execute(
                // doWithRetry 메소드
                retryContext -> {
                    if (item.equals("1") || item.equals("2")) {
                        cnt++;
                        throw new RetryableException("failed cnt = " + cnt);
                    }
                    return new Item(item);
                },
                // recover 메소드
                new RecoveryCallback<Item>() {
                    @Override
                    public Item recover(RetryContext retryContext) throws Exception {
                        return new Item(item);
                    }
                }, new DefaultRetryState(item, rollbackClassifier)
        );
        return returnItem;
    }
}
