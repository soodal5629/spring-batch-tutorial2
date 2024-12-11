package io.spring.springbatch.practicalexmaple.service;

import io.spring.springbatch.practicalexmaple.domain.ApiInfo;
import io.spring.springbatch.practicalexmaple.domain.ApiResponseVO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService3 extends AbstractApiService {

    @Override
    protected ApiResponseVO doApiService(RestTemplate restTemplate, ApiInfo apiInfo) {
        ResponseEntity<String> responseEntity =
                restTemplate.postForEntity("http://localhost:8081/api/product/3", apiInfo, String.class);
        int statusCodeValue = responseEntity.getStatusCode().value();

        return ApiResponseVO.builder()
                .status(statusCodeValue)
                .message(responseEntity.getBody())
                .build();
    }
}
