package io.spring.springbatch.simplejob;

import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.JobParametersValidator;

public class CustomJobParametersValidator implements JobParametersValidator {

    // 검증 2번 함: 1. job이 수행되기 전 jobRepository 기능 수행되기 전, 2. job 실행되기 전
    @Override
    public void validate(JobParameters parameters) throws JobParametersInvalidException {
        if(parameters.getString("name") == null) {
            throw new JobParametersInvalidException("name parameter is not found");
        }
    }
}
