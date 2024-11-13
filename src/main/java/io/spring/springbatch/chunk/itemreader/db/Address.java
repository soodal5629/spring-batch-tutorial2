package io.spring.springbatch.chunk.itemreader.db;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = "customer")
@Entity
public class Address {
    @Id @GeneratedValue
    @Column(name = "address_id")
    private Long id;
    private String location;

    @OneToOne
    @JoinColumn(name = "id")
    @JsonIgnore
    private Customer customer;
}
