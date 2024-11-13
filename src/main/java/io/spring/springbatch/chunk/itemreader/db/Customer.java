package io.spring.springbatch.chunk.itemreader.db;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Customer {
    @Id @GeneratedValue
    private Long id;
    @Column(name = "firstname")
    private String firstName;
    @Column(name = "lastname")
    private String lastName;
    private String birthdate;

    @OneToOne(mappedBy = "customer")
    private Address address;
}
