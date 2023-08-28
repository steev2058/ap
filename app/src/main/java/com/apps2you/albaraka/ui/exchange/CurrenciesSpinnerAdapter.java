package com.apps2you.albaraka.ui.exchange;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.data.model.ExchangeRate;
import com.apps2you.albaraka.databinding.ItemCurrencySpinnerBinding;
import com.apps2you.albaraka.databinding.ItemCurrencySpinnerDropdownBinding;

import java.util.ArrayList;


public class CurrenciesSpinnerAdapter extends BaseAdapter {

    private final ArrayList<ExchangeRate> items=new ArrayList<>();

    public CurrenciesSpinnerAdapter(ArrayList<ExchangeRate> items) {
        this.items.addAll(items);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            ItemCurrencySpinnerBinding itemSpinnerBinding = ItemCurrencySpinnerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new ItemViewHolder(itemSpinnerBinding);
            holder.itemView.setTag(holder);
        } else {
            holder = (ItemViewHolder) convertView.getTag();
        }

        holder.onBind(getItem(position));

        return holder.itemView;
    }
//
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        DropItemViewHolder holder;
        if (convertView == null) {
            ItemCurrencySpinnerDropdownBinding itemSpinnerBinding = ItemCurrencySpinnerDropdownBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            holder = new DropItemViewHolder(itemSpinnerBinding);
            holder.itemView.setTag(holder);
        } else {
            holder = (DropItemViewHolder) convertView.getTag();
        }

        holder.onBind(getItem(position));

        return holder.itemView;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public ExchangeRate getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(ArrayList<ExchangeRate> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        final ItemCurrencySpinnerBinding binding;

        public ItemViewHolder(ItemCurrencySpinnerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void onBind(ExchangeRate item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }


    class DropItemViewHolder extends RecyclerView.ViewHolder {

        final ItemCurrencySpinnerDropdownBinding binding;

        public DropItemViewHolder(ItemCurrencySpinnerDropdownBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void onBind(ExchangeRate item) {

            binding.setItem(item);
            binding.executePendingBindings();

        }
    }
}
