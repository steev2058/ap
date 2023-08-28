package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.BankUI;

import javax.inject.Inject;

public class BankUIMapper extends Mapper<BankUI, Partner> {

    @Inject
    public BankUIMapper() {
    }

    @Override
    public BankUI map(Partner model) {
        return new BankUI(model.getId(), model.getName(), model.getLogo(), model.getCode());
    }
}
