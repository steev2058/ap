package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.CharityUI;

import javax.inject.Inject;

public class CharityUIMapper extends Mapper<CharityUI, Partner> {

    @Inject
    public CharityUIMapper() {
    }

    @Override
    public CharityUI map(Partner model) {
        return new CharityUI(model.getId(), model.getName(), model.getLogo(), model.getAccountNumber());
    }
}
