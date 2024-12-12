package io.spring.springbatch.practicalexmaple.chunck.writer;

import io.spring.springbatch.practicalexmaple.domain.ApiRequestVO;
import io.spring.springbatch.practicalexmaple.domain.ApiResponseVO;
import io.spring.springbatch.practicalexmaple.service.AbstractApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;

@Slf4j
public class ApiItemWriter2 implements ItemWriter<ApiRequestVO> {
    private final AbstractApiService apiService;

    public ApiItemWriter2(AbstractApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public void write(Chunk<? extends ApiRequestVO> chunk) throws Exception {
        ApiResponseVO response = apiService.service(chunk);
        log.info("write2 response = {}", response);
    }
}
