package com.apps2you.albaraka.ui.common.model.mapper;

import com.apps2you.albaraka.data.model.Partner;
import com.apps2you.albaraka.ui.common.model.SEPProviderUI;

import javax.inject.Inject;

public class SEPProviderUIMapper extends Mapper<SEPProviderUI, Partner> {

@Inject
public SEPProviderUIMapper() {
        }

@Override
public SEPProviderUI map(Partner model) {
        return new SEPProviderUI(model.getId(), model.getName(), model.getLogo());
        }
        }