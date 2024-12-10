package io.spring.springbatch.practicalexmaple.chunck.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.springbatch.practicalexmaple.domain.Product;
import io.spring.springbatch.practicalexmaple.domain.ProductVO;
import org.springframework.batch.item.ItemProcessor;

public class FileItemProcessor implements ItemProcessor<ProductVO, Product> {

    @Override
    public Product process(ProductVO item) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.convertValue(item, Product.class);
        return new Product(item.getName(), item.getPrice(), item.getType());
    }
}
