package com.apps2you.albaraka.ui.common.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.ItemAccountSpinnerBinding;
import com.apps2you.albaraka.databinding.ItemAccountSpinnerDropdownBinding;

import java.util.ArrayList;


public class AccountsSpinnerAdapter extends BaseAdapter {

    private final ArrayList<Account> items;

    public AccountsSpinnerAdapter() {
        items = new ArrayList<>();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewHolder holder;
        if (convertView == null) {
            ItemAccountSpinnerBinding itemSpinnerBinding = ItemAccountSpinnerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
            ItemAccountSpinnerDropdownBinding itemSpinnerBinding = ItemAccountSpinnerDropdownBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
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
    public Account getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addAll(ArrayList<Account> items) {
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        final ItemAccountSpinnerBinding binding;

        public ItemViewHolder(ItemAccountSpinnerBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        private void onBind(Account item) {
            binding.setItem(item);
            binding.executePendingBindings();
        }
    }


    class DropItemViewHolder extends RecyclerView.ViewHolder {

        final ItemAccountSpinnerDropdownBinding binding;

        public DropItemViewHolder(ItemAccountSpinnerDropdownBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void onBind(Account item) {

            binding.setItem(item);
            binding.executePendingBindings();

        }
    }
}
