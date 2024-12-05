package io.spring.springbatch.eventlistener;

public class CustomSkipException extends RuntimeException {
    public CustomSkipException() { super(); }
    public CustomSkipException(String message) { super(message); }
}
