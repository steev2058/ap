package com.apps2you.albaraka.ui.common.list.listlivedata;

import androidx.lifecycle.MediatorLiveData;

import com.apps2you.albaraka.ui.common.list.listlivedata.event.AddEvent;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ClearAllEvent;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ListEvent;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.RemoveIndexEvent;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ReplaceDataEvent;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.UpdateEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * To observe this list there is a helper class ListObserver
 * to manage the list events.
 */
public class ListLiveData<T> extends MediatorLiveData<ListEvent<T>> {

    protected final List<T> data = new ArrayList<>();


    public void add(T item) {
        data.add(item);

        List<T> dataToAdd = new ArrayList<>();
        dataToAdd.add(item);

        setValue(new AddEvent<>(data, dataToAdd));
    }

    public void addAll(List<T> items) {
        data.addAll(items);
        setValue(new AddEvent<>(data, items));
    }

    public void remove(int index) {
        data.remove(index);
        setValue(new RemoveIndexEvent<>(data, index));
    }

    public void update(T updatedItem) {
        int index = data.indexOf(updatedItem);
        if (index >= 0) {
            data.set(index, updatedItem);
        }
        setValue(new UpdateEvent<>(data, updatedItem));
    }

    public void replaceData(List<T> newItems) {
        data.clear();
        data.addAll(newItems);
        setValue(new ReplaceDataEvent<>(data, newItems));
    }

    public void clear() {
        data.clear();
        setValue(new ClearAllEvent<>(null));
    }

    public List<T> getData() {
        return data;
    }

    public int size() {
        return data.size();
    }
}
