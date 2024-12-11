package io.spring.springbatch.practicalexmaple.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiRequestVO {
    private long id;
    private ProductVO productVO;
}
