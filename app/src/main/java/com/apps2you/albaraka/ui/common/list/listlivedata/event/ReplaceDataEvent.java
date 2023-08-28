package com.apps2you.albaraka.ui.common.list.listlivedata.event;

import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;


/**
 * Clears the old data then add the new one.
 */
public class ReplaceDataEvent<T> extends ListEvent<T> {

    private final List<T> newData;

    public ReplaceDataEvent(List<T> allData, List<T> newData) {
        super(allData);
        this.newData = newData;
    }

    @Override
    protected void applyEvent(BaseListAdapter<T, ?> adapter) {
        adapter.submitData(newData);
    }
}
