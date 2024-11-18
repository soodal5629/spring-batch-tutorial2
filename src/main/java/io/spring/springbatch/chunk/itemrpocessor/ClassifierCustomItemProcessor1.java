package io.spring.springbatch.chunk.itemrpocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

@Slf4j
public class ClassifierCustomItemProcessor1 implements ItemProcessor<ProcessorInfo, ProcessorInfo> {

    @Override
    public ProcessorInfo process(ProcessorInfo item) throws Exception {
        log.info("### classifier processor1");
        return item;
    }
}
