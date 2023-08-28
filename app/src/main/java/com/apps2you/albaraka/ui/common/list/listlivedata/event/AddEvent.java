package com.apps2you.albaraka.ui.common.list.listlivedata.event;


import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;

/**
 * Adds new data to the existing data.
 */
public class AddEvent<T> extends ListEvent<T> {

    private final List<T> dataToAdd;

    public AddEvent(List<T> allData, List<T> dataToAdd) {
        super(allData);
        this.dataToAdd = dataToAdd;
    }

    @Override
    protected void applyEvent(BaseListAdapter<T, ?> adapter) {
        adapter.insertData(dataToAdd);
    }
}
