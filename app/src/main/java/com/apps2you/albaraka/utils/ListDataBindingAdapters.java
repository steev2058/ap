package com.apps2you.albaraka.utils;

import android.widget.Spinner;

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.base.adapter.list.PagingListAdapter;
import com.apps2you.albaraka.ui.common.list.listlivedata.event.ListEvent;
import com.apps2you.albaraka.ui.complaints.fragments.SpinnerAdapter;

import java.util.ArrayList;
import java.util.List;

public final class ListDataBindingAdapters {

    private ListDataBindingAdapters() {
    }


    @BindingAdapter("android:bottomLoader")
    public static void listBottomLoader(RecyclerView recyclerView, boolean isLoading) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof PagingListAdapter) {
            if (isLoading)
                ((PagingListAdapter<?>) adapter).addBottomLoader();
            else
                ((PagingListAdapter<?>) adapter).removeBottomLoader();
        }
    }

    @BindingAdapter("android:observeListEvents")
    public static void loadData(RecyclerView recyclerView, ListEvent<?> listEvent) {
        if (listEvent == null)
            return;
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof BaseListAdapter) {
            listEvent.attach((BaseListAdapter) adapter);
        }
    }

    @BindingAdapter("android:loadData")
    public static void loadData(ViewPager2 pager, List<?> data) {
        RecyclerView.Adapter<?> adapter = pager.getAdapter();
        if (adapter instanceof BaseListAdapter) {
            ((BaseListAdapter) adapter).submitData(data);
        }
    }

    @BindingAdapter("android:loadData")
    public static void loadRecyclerData(RecyclerView recyclerView, List<?> data) {
        RecyclerView.Adapter<?> adapter = recyclerView.getAdapter();
        if (adapter instanceof BaseListAdapter) {
            ((BaseListAdapter) adapter).submitData(data);
        }
    }

    @BindingAdapter("android:loadData")
    public static void loadSpinnerData(Spinner spinner, ArrayList<?> data) {
        if (data != null) {
            SpinnerAdapter adapter = new SpinnerAdapter(data);
            spinner.setAdapter(adapter);
        }
    }
}
