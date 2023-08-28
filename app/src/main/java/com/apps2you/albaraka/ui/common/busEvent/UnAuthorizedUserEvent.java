package com.apps2you.albaraka.ui.common.busEvent;


import com.apps2you.albaraka.utils.bus.BusEvent;

public class UnAuthorizedUserEvent extends BusEvent {

    private UnAuthorizedUserEvent(Object eventTrigger) {
        super(eventTrigger);
    }

    public static UnAuthorizedUserEvent getInstance() {
        return new UnAuthorizedUserEvent(null);
    }
}
