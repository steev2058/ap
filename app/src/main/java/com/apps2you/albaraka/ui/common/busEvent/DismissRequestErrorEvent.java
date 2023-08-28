package com.apps2you.albaraka.ui.common.busEvent;

import android.app.Activity;

import androidx.annotation.Nullable;

import com.apps2you.albaraka.utils.bus.BusEvent;

public class DismissRequestErrorEvent extends BusEvent {
    private final Exception error;

    public DismissRequestErrorEvent(@Nullable Activity activity, Exception error) {
        super(activity);
        this.error = error;
    }

    public Exception getError() {
        return error;
    }
}
