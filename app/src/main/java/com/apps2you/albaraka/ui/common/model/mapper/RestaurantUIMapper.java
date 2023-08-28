package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.RestaurantUI;

import javax.inject.Inject;

public class RestaurantUIMapper extends Mapper<RestaurantUI, Partner> {

    @Inject
    public RestaurantUIMapper() {
    }

    @Override
    public RestaurantUI map(Partner model) {
        return new RestaurantUI(model.getId(), model.getName(), model.getLogo(), model.getAccountNumber());
    }
}
