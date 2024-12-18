package io.spring.springbatch.practicalexmaple.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVO implements Serializable {
    private Long id;
    private String name;
    private Integer price;
    private String type;
}
