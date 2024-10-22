package io.spring.springbatch.chunk;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.*;

import java.util.List;

@Slf4j
public class CustomItemStreamReader implements ItemStreamReader<String> {
    private final List<String> items;
    private int index = -1;
    private boolean restart = false;

    public CustomItemStreamReader(List<String> items) {
        this.items = items;
        this.index = 0;
    }

    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        String item = null;
        if (this.index < this.items.size()) {
            item = this.items.get(this.index);
            index++;
        }
        if(this.index == 6 && !restart) {
            throw new RuntimeException("Restart is required");
        }
        return item;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        // index 값 초기화 및 db에 저장
        if(executionContext.containsKey("index")) {
            index = executionContext.getInt("index");
            this.restart = true;
        } else {
            index = 0;
            executionContext.put("index", index);
        }
        ItemStreamReader.super.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        // 상태 정보 저장 -> job 재시작 시 마지막 index 가져오도록
        executionContext.put("index", index);
    }

    @Override
    public void close() throws ItemStreamException {
        log.info(">>> close");
        ItemStreamReader.super.close();
    }
}
