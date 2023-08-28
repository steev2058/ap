package com.apps2you.albaraka.ui.common.list.listlivedata.event;


import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;

public abstract class ListEvent<T> {

    private final List<T> allData;

    public ListEvent(List<T> allData) {
        this.allData = allData;
    }

    public void attach(BaseListAdapter<T, ?> adapter) {
        if (adapter.isEmptyOrNullData()) {
            adapter.submitData(allData);
        } else {
            applyEvent(adapter);
        }
    }

    protected abstract void applyEvent(BaseListAdapter<T, ?> adapter);

    public int allDataSize() {
        return allData == null ? 0 : allData.size();
    }

    public List<T> getAllData() {
        return allData;
    }
}
