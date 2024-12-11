package io.spring.springbatch.practicalexmaple.chunck.writer;

import io.spring.springbatch.practicalexmaple.domain.ApiRequestVO;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

public class ApiItemWriter3 implements ItemWriter<ApiRequestVO> {
    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {

    }
}
