package com.apps2you.albaraka.ui.common.busEvent;

import com.apps2you.albaraka.utils.bus.BusEvent;

public class RefreshAccountsEvent extends BusEvent {

    private RefreshAccountsEvent(Object eventTrigger) {
        super(eventTrigger);
    }

    public static RefreshAccountsEvent getInstance() {
        return new RefreshAccountsEvent(null);
    }
}
