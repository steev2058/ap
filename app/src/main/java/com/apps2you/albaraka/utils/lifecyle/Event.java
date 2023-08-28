package com.apps2you.albaraka.utils.lifecyle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


/**
 * Data Wrapper that can be handled only once
 *
 * @param <T> data type
 */
public class Event<T> {
    @NonNull
    private T data;

    private boolean isHandled = false;

    public Event(@NonNull T data) {
        this.data = data;
    }

    public static <T> Event<T> of (@NonNull T data) {
        return new Event<>(data);
    }

    @Nullable
    public T value() {
        return data;
    }

    @Nullable
    T getDataOrNull() {
        if (isHandled)
            return null;
        isHandled = true;
        return data;
    }
}
