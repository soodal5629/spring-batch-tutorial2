package io.spring.springbatch.chunk.itemWriter.db.jpa;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.spring.springbatch.chunk.itemreader.db.Customer;
import org.springframework.batch.item.ItemProcessor;

public class CustomJpaItemWriterProcessor implements ItemProcessor<Customer, Customer2> {

    // Customer 객체를 Customer2 엔티티 객체로 변환해줘야 함
    @Override
    public Customer2 process(Customer item) throws Exception {
//        ObjectMapper mapper = new ObjectMapper();
//        return mapper.convertValue(item, Customer2.class);
        return Customer2.builder()
                // Entity에 @Id @Generated 설정을 해놨기 때문에 .id()를 통해 id를 설정해버리면 PersistentObjectException 발생
                //.id(item.getId())
                .firstName(item.getFirstName())
                .lastName(item.getLastName())
                .birthdate(item.getBirthdate())
                .build();
    }
}
