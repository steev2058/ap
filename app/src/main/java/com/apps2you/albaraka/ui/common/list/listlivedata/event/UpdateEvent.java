package com.apps2you.albaraka.ui.common.list.listlivedata.event;

import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;

public class UpdateEvent<T> extends ListEvent<T>{
    private final T updatedItem;

    public UpdateEvent(List<T> allData, T updatedItem) {
        super(allData);
        this.updatedItem = updatedItem;
    }

    @Override
    protected void applyEvent(BaseListAdapter<T, ?> adapter) {
        adapter.updateItem(updatedItem);
    }
}
