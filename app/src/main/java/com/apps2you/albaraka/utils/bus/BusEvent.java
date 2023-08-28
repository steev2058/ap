package com.apps2you.albaraka.utils.bus;

import androidx.annotation.Nullable;

public abstract class BusEvent {

    public final Object eventTrigger;

    public BusEvent(@Nullable Object eventTrigger) {
        this.eventTrigger = eventTrigger;
    }
}
