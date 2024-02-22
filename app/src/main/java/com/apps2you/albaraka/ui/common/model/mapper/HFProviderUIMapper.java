package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.HFProviderUI;

import javax.inject.Inject;

public class HFProviderUIMapper extends Mapper<HFProviderUI, Partner> {

    @Inject
    public HFProviderUIMapper() {
    }

    @Override
    public HFProviderUI map(Partner model) {
        return new HFProviderUI(model.getId(), model.getName(), model.getLogo(),model.getCode());
    }
}
