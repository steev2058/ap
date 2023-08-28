package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.SchoolUI;

import javax.inject.Inject;

public class SchoolUIMapper extends Mapper<SchoolUI, Partner> {

    @Inject
    public SchoolUIMapper() {
    }

    @Override
    public SchoolUI map(Partner model) {
        return new SchoolUI(model.getId(), model.getName(), model.getLogo(), model.getAccountNumber());
    }
}
