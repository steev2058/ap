package com.apps2you.albaraka.data.exception;

public class NoInternetException extends Exception {

    public NoInternetException() {
    }

    public NoInternetException(String message) {
        super(message);
    }

    public NoInternetException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoInternetException(Throwable cause) {
        super(cause);
    }
}
