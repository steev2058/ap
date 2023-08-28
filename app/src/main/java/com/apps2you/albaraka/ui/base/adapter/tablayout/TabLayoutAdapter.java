package com.apps2you.albaraka.ui.base.adapter.tablayout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

public abstract class TabLayoutAdapter<T, VH extends TabLayoutViewHolder<T, ?>> {

    protected Context context;
    protected TabLayout tabLayout;

    protected List<T> data;

    private final List<TabLayoutViewHolder<T, ?>> viewHolders = new ArrayList<>();

    public TabLayoutAdapter(Context context, TabLayout tabLayout) {
        this.context = context;
        this.tabLayout = tabLayout;
        addTabSelectedListener();
    }

    public abstract VH onCreateTab(int position, ViewGroup parent);

    public void onBindTab(VH viewHolder, int position) {
        viewHolder.onBind(data.get(position), position);
    }

    public int getItemCount() {
        return data != null ? data.size() : 0;
    }

    protected <K extends ViewDataBinding> K inflate(int layoutId, ViewGroup parent) {
        return DataBindingUtil.inflate(provideInflater(), layoutId, parent, false);
    }

    protected void createTabs() {
        for (int i = 0; i < getItemCount(); i++) {
            VH tabViewHolder = onCreateTab(i, null);
            View customView = tabViewHolder.binding.getRoot();
            onBindTab(tabViewHolder, i);
            viewHolders.add(tabViewHolder);
            TabLayout.Tab tab = tabLayout.newTab().setCustomView(customView);
            tabLayout.addTab(tab);
        }
    }

    protected LayoutInflater provideInflater() {
        return LayoutInflater.from(context);
    }

    protected void addTabSelectedListener() {
        tabLayout.addOnTabSelectedListener(tabSelectedListener);
    }

    protected void removeTabSelectedListener() {
        tabLayout.removeOnTabSelectedListener(tabSelectedListener);
    }

    public void destroy() {
        removeTabSelectedListener();
    }

    public void submitData(List<T> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    public void notifyDataSetChanged() {
        tabLayout.removeAllTabs();
        viewHolders.clear();
        createTabs();
    }

    public List<T> getData() {
        return data;
    }

    public TabLayoutViewHolder<T, ?> findViewHolderByPosition(int position) {
        if (position >= viewHolders.size())
            return null;
        return viewHolders.get(position);
    }

    private final TabLayout.OnTabSelectedListener tabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            changeSelected(tab.getPosition(), true);
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {
            changeSelected(tab.getPosition(), false);
        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {
            onTabSelected(tab);
        }

        private void changeSelected(int position, boolean isSelected) {
            if (position < viewHolders.size())
                viewHolders.get(position).onSelectedChanged(isSelected);
        }
    };

    public interface OnTabClicked {
        void onTabClicked(int position);
    }
}
