package io.spring.springbatch.practicalexmaple.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Product {
    @Id @GeneratedValue
    private Long id;
    private String name;
    private Integer price;
    private String type;

    public Product(String name, Integer price, String type) {
        this.name = name;
        this.price = price;
        this.type = type;
    }

}
