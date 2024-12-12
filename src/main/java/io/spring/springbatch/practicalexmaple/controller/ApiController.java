package io.spring.springbatch.practicalexmaple.controller;

import io.spring.springbatch.practicalexmaple.domain.ApiInfo;
import io.spring.springbatch.practicalexmaple.domain.ProductVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
public class ApiController {
    // 각 컨트롤러는 다른 서버에 떠있다고 가정하여 @RequestParam {id} 을 이용하지 않고 request를 각각 따로 만듦
    @PostMapping("/api/product/1")
    public String product1(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> list = new ArrayList<>();
        apiInfo.getApiRequestList().forEach(apiRequestVO -> list.add(apiRequestVO.getProductVO()));
        log.info("list1 = {}", list);
        return "product1 was successfully processed";
    }

    @PostMapping("/api/product/2")
    public String product2(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> list = new ArrayList<>();
        apiInfo.getApiRequestList().forEach(apiRequestVO -> list.add(apiRequestVO.getProductVO()));
        log.info("list2 = {}", list);
        return "product2 was successfully processed";
    }

    @PostMapping("/api/product/3")
    public String product3(@RequestBody ApiInfo apiInfo) {
        List<ProductVO> list = new ArrayList<>();
        apiInfo.getApiRequestList().forEach(apiRequestVO -> list.add(apiRequestVO.getProductVO()));
        log.info("list3 = {}", list);
        return "product3 was successfully processed";
    }
}
