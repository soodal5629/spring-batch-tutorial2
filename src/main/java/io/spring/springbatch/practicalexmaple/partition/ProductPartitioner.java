package io.spring.springbatch.practicalexmaple.partition;

import io.spring.springbatch.practicalexmaple.domain.ProductVO;
import io.spring.springbatch.practicalexmaple.job.api.QueryGenerator;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ExecutionContext;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

public class ProductPartitioner implements Partitioner {
    private final DataSource dataSource;

    public ProductPartitioner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Map<String, ExecutionContext> partition(int gridSize) {
        // product type의 개수만큼 스레드 생성
        ProductVO[] productList = QueryGenerator.getProductList(dataSource);
        Map<String, ExecutionContext> result = new HashMap<>();
        int num = 0;
        for (int i = 0; i < productList.length; i++) {
            ExecutionContext value = new ExecutionContext();
            value.put("product", productList[i]);
            result.put("partition" + num, value);
            num++;
        }
        return result;
    }
}
