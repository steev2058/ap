package com.apps2you.albaraka.ui.base.adapter.tablayout;

import androidx.databinding.ViewDataBinding;

public abstract class TabLayoutViewHolder<T, DB extends ViewDataBinding> {
    protected DB binding;

    public TabLayoutViewHolder(DB binding) {
        this.binding = binding;
    }

    public abstract void onBind(T item, int position);

    public abstract void onSelectedChanged(boolean isSelected);
}