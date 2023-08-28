package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.UniversityUI;

import javax.inject.Inject;

public class UniversityUIMapper extends Mapper<UniversityUI, Partner> {

    @Inject
    public UniversityUIMapper() {
    }

    @Override
    public UniversityUI map(Partner model) {
        return new UniversityUI(model.getId(), model.getName(), model.getLogo(), model.getAccountNumber());
    }
}
