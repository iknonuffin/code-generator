package org.codegen.exception;

public class InitializrException extends RuntimeException {

    public InitializrException(String message) {
        super(message);
    }

    public InitializrException(String message, Throwable cause) {
        super(message, cause);
    }
}
