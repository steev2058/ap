package com.apps2you.albaraka.ui.base.adapter.list;

import android.content.Context;
import android.os.Handler;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import java.util.List;

public abstract class PagingListAdapter<T> extends BaseListAdapter<T, BaseViewHolder<T, ?>> {

    private final static int FILLED = 1;
    private final static int LOADING = 2;

    private final OnLoadPageListener onLoadPageListener;

    public PagingListAdapter(Context context, OnLoadPageListener onLoadPageListener) {
        super(context);
        this.onLoadPageListener = onLoadPageListener;
    }


    public abstract BaseViewHolder<T, ?> onCreateFilledViewHolder(@NonNull ViewGroup parent, int viewType);

    public abstract BaseViewHolder onCreateLoaderViewHolder(@NonNull ViewGroup parent, int viewType);

    @Override
    public int getItemViewType(int position) {
        if (getData() != null && getData().get(position) == null) // The null object is the loader.
            return LOADING;
        return FILLED;
    }

    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<T, ?> holder, int position) {
        if (getItemViewType(position) == FILLED) {
            super.onBindViewHolder(holder, position);
            if (position == getData().size() - 1)
                onLoadPageListener.onLoadPage();
        }
    }

    @NonNull
    @Override
    public BaseViewHolder<T, ?> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == LOADING) {
            return onCreateLoaderViewHolder(parent, viewType);
        } else // FILLED type
            return onCreateFilledViewHolder(parent, viewType);
    }

    protected boolean hasBottomLoader() {
        return !isEmptyOrNullData() && getData().get(getData().size() - 1) == null;
    }

    public void addBottomLoader() {
        if (isEmptyOrNullData() || hasBottomLoader())
            return;
        getData().add(null); // add loader
        new Handler().post(() -> notifyItemInserted(getData().size() - 1));
    }

    public void removeBottomLoader() {
        if (isEmptyOrNullData())
            return;
        List<T> data = getData();
        T lastItem = getData().get(data.size() - 1);
        if (lastItem == null) { // this is the loader
            getData().remove(data.size() - 1);
            notifyItemRemoved(data.size() - 1);
        }
    }

    @Override
    public void submitData(List<T> data) {
        removeBottomLoader();
        super.submitData(data);
    }

    @Override
    public void insertData(List<T> data) {
        removeBottomLoader();
        super.insertData(data);
    }
}
