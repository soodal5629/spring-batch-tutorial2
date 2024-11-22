package io.spring.springbatch.chunk.control;

public class RetryableException extends RuntimeException {
    public RetryableException() {
    }

    public RetryableException(String message) {
        super(message);
    }
}
