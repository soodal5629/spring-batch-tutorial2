package io.spring.springbatch.practicalexmaple.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.batch.item.Chunk;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiInfo {
    private String url;
    // api 서버로 보낼 request 객체(api 서버 별도 존재한다고 가정)
    private Chunk<? extends ApiRequestVO> apiRequestList;
}
