package com.apps2you.albaraka.ui.transfer.payment.restaurants;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.RestaurantUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.payment.restaurants.RestaurantsPaymentViewModel;

public class RestaurantSelectionFragment extends BaseSelectionFragment<RestaurantUI, RestaurantsPaymentViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return requireActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.restaurants);
    }

    @Override
    public Class<RestaurantsPaymentViewModel> setViewModel() {
        return RestaurantsPaymentViewModel.class;
    }
}
