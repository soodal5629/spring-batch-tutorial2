package io.spring.springbatch.practicalexmaple.chunck.processor;

import io.spring.springbatch.practicalexmaple.domain.ApiRequestVO;
import io.spring.springbatch.practicalexmaple.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class ApiItemProcessor2 implements ItemProcessor<ProductVO, ApiRequestVO> {
    @Override
    public ApiRequestVO process(ProductVO item) throws Exception {
        return ApiRequestVO.builder()
                .id(item.getId())
                .productVO(item)
                .build();
    }
}
