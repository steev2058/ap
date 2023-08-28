package com.apps2you.albaraka.ui.common.list.listlivedata.event;


import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;

public class ClearAllEvent<T> extends ListEvent<T> {

    public ClearAllEvent(List<T> allData) {
        super(allData);
    }

    public ClearAllEvent() {
        this(null);
    }

    @Override
    protected void applyEvent(BaseListAdapter<T, ?> adapter) {
        adapter.clearData();
    }
}
