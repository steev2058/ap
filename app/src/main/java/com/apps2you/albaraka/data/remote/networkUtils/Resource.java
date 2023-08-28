package com.apps2you.albaraka.data.remote.networkUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import static com.apps2you.albaraka.data.remote.networkUtils.Status.ERROR;
import static com.apps2you.albaraka.data.remote.networkUtils.Status.LOADING;
import static com.apps2you.albaraka.data.remote.networkUtils.Status.SUCCESS;


public class Resource<T> {

    @NonNull
    public final Status status;

    @Nullable
    public final T data;

    @Nullable
    public final String message;

    @Nullable
    public final Exception error;

    public final int code;

    public Resource(@NonNull Status status, @Nullable T data, @Nullable String message, @NonNull int code, Exception error) {
        this.status = status;
        this.data = data;
        this.message = message;
        this.code = code;
        this.error = error;
    }

    public static <T> Resource<T> success(@NonNull T data, String message) {
        return new Resource<>(SUCCESS, data, message, 0, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, int code) {
        return new Resource<>(ERROR, data, msg, code, null);
    }

    public static <T> Resource<T> error(String msg, @Nullable T data, int code, Exception error) {
        return new Resource<>(ERROR, data, msg, code, error);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(LOADING, data, null, 0, null);
    }


    public static <K, T> Resource<K> mapData(Resource<T> resource, ResourceMapper<T, K> mapper) {
        return new Resource<>(resource.status, mapper.map(resource.data), resource.message, resource.code, resource.error);
    }

    @Nullable
    public String getMessage() {
        return message;
    }


    public <K> Resource<K> mapData(ResourceMapper<T, K> mapper) {
        return mapData(this, mapper);
    }

    public interface ResourceMapper<T, K> {
        K map(T data);
    }
}
