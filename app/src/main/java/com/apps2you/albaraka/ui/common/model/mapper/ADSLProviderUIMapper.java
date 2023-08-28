package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.ADSLProviderUI;

import javax.inject.Inject;

public class ADSLProviderUIMapper extends Mapper<ADSLProviderUI, Partner> {

    @Inject
    public ADSLProviderUIMapper() {
    }

    @Override
    public ADSLProviderUI map(Partner model) {
        return new ADSLProviderUI(model.getId(), model.getName(), model.getLogo());
    }
}
