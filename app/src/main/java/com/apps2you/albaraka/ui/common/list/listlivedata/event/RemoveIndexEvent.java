package com.apps2you.albaraka.ui.common.list.listlivedata.event;

import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;

import java.util.List;

/**
 * Removes the item in the passed index.
 */
public class RemoveIndexEvent<T> extends ListEvent<T> {

    private final int indexToRemove;

    public RemoveIndexEvent(List<T> allData, int indexToRemove) {
        super(allData);
        this.indexToRemove = indexToRemove;
    }

    @Override
    protected void applyEvent(BaseListAdapter<T, ?> adapter) {
        adapter.removeItem(indexToRemove);
    }
}
