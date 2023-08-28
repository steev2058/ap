package com.apps2you.albaraka.data.remote.networkUtils;

import com.google.gson.annotations.SerializedName;

public class MyResponse<T> {

    @SerializedName("message")
    protected String message;

    @SerializedName("data")
    protected T data;

    public static class ErrorResponse {
        @SerializedName("message")
        public String message;
    }

    public MyResponse(String message) {
        this.message = message;
    }

    public MyResponse() {
    }

    public MyResponse(String message, T data) {
        this.message = message;
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
