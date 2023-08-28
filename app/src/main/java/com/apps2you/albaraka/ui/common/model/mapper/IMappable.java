package com.apps2you.albaraka.ui.common.model.mapper;

public interface IMappable<TO, FROM> {

    TO map(FROM model);
}
