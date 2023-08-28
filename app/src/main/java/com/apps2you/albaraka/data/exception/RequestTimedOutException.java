package com.apps2you.albaraka.data.exception;

import java.util.concurrent.TimeoutException;

public class RequestTimedOutException extends TimeoutException {

    public RequestTimedOutException() {
    }

    public RequestTimedOutException(String message) {
        super(message);
    }

}
