package io.spring.springbatch.eventlistener;

public class CustomRetryException extends Exception {
    public CustomRetryException() {
    }

    public CustomRetryException(String message) {
        super(message);
    }
}
