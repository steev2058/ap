package com.apps2you.albaraka.ui.transfer.payment.restaurants;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.transfer.payment.BasePaymentActivity;

public class RestaurantsPaymentActivity extends BasePaymentActivity {
    @Override
    protected int provideNaveGraphId() {
        return R.navigation.nav_restaurants;
    }
}
