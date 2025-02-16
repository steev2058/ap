package com.apps2you.albaraka.ui.base.adapter.list;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

abstract public class BaseListAdapter<T, VH extends BaseViewHolder<T, ?>>
        extends RecyclerView.Adapter<VH> {

    protected Context context;
    private List<T> data;

    public BaseListAdapter(Context context) {
        this.context = context;
        setHasStableIds(true);
    }

//    @Override
//    public void onBindViewHolder(@NonNull VH holder, int position) {
//        T item = data != null && data.size() > position ? data.get(position) : null;
//        holder.onBind(item, position);
//    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        if (data == null || position >= data.size()) return;

        T item = data.get(position);

        // Reset UI elements to prevent old data from interfering
        holder.itemView.setBackgroundColor(Color.TRANSPARENT); // Example
        holder.itemView.setAlpha(1.0f);

        holder.onBind(item, position);
    }

    @Override
    public long getItemId(int position) {
        return data.get(position).hashCode(); // Use unique identifier
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public List<T> getData() {
        return data;
    }

    public LayoutInflater provideInflater() {
        return LayoutInflater.from(context);
    }

    protected <K extends ViewDataBinding> K inflate(int layoutId, ViewGroup parent, boolean attachToParent) {
        return DataBindingUtil.inflate(provideInflater(), layoutId, parent, attachToParent);
    }

    // Clear previous data, then add the new data
    public void submitData(List<T> data) {
        if (data == null)
            return;
        if (this.data == null)
            this.data = new ArrayList<>();
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    // Add the data to old data
    public void insertData(List<T> data) {
        if (data == null)
            return;
        if (this.data == null)
            this.data = new ArrayList<>();
        int insertIndex = this.data.size();
        this.data.addAll(data);
        notifyItemRangeInserted(insertIndex, data.size());
    }

    public void insertItem(T item) {
        if (this.data == null)
            this.data = new ArrayList<>();
        this.data.add(item);
        notifyItemInserted(data.size() - 1);
    }

    public void removeItem(int index) {
        if (index < 0)
            return;
        data.remove(index);
        notifyItemRemoved(index);
        notifyItemRangeChanged(index, data.size() - 1);
    }

    public void removeItem(T item) {
        if (item == null || data == null) return;

        int index = data.indexOf(item);

        removeItem(index);
    }

    public void updateItem(T item) {
        if (item == null || data == null) return;

        int index = data.indexOf(item);

        if (index < 0)
            return;

        data.set(index, item);
        notifyItemChanged(index);
    }

    public void clearData() {
        if (data == null)
            return;
        data.clear();
        notifyDataSetChanged();
    }

    public boolean isEmptyOrNullData() {
        return data == null || data.isEmpty();
    }
}







