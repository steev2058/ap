package com.apps2you.albaraka.ui.common.busEvent;

import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.utils.bus.BusEvent;

public class FavoriteAccountSelectedEvent extends BusEvent {
    private final FavoriteAccount selectedAccount;

    public FavoriteAccountSelectedEvent(FavoriteAccount selectedAccount) {
        super(null);
        this.selectedAccount = selectedAccount;
    }

    public FavoriteAccount getSelectedAccount() {
        return selectedAccount;
    }
}
