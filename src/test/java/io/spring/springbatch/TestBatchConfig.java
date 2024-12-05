package io.spring.springbatch;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;

//@Configuration - 없어도 되네
@EnableAutoConfiguration
@EnableBatchProcessing
public class TestBatchConfig {

}
