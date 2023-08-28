package com.apps2you.albaraka.ui.common.list.listlivedata;


import androidx.lifecycle.Observer;

import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ListEvent;

public class ListObserver<T> implements Observer<ListEvent<T>> {

    private final BaseListAdapter<T, ?> adapter;

    public ListObserver(BaseListAdapter<T, ?> adapter) {
        this.adapter = adapter;
    }

    @Override
    public void onChanged(ListEvent<T> event) {
        event.attach(adapter);
    }
}
