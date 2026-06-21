package ru.skfu.carrental.exception;

public class ExternalGatewayException extends RuntimeException {
    public ExternalGatewayException(String message) {
        super(message);
    }
}
