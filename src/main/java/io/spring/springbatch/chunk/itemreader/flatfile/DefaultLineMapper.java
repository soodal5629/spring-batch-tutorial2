package io.spring.springbatch.chunk.itemreader.flatfile;

import lombok.Setter;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.LineTokenizer;

@Setter
public class DefaultLineMapper<T> implements LineMapper<T> {
    private LineTokenizer tokenizer;
    private FieldSetMapper<T> fieldSetMapper;

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        // tokenize 메소드에서 DefaultFieldSet(스프링 배치 제공) 리턴
        return fieldSetMapper.mapFieldSet(tokenizer.tokenize(line));
    }
}
