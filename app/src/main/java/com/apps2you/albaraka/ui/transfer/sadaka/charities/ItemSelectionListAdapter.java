package com.apps2you.albaraka.ui.transfer.sadaka.charities;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ItemSelectionBinding;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.base.adapter.list.BaseViewHolder;
import com.apps2you.albaraka.ui.common.model.SelectableItem;

import org.jetbrains.annotations.NotNull;

public class ItemSelectionListAdapter<Model extends SelectableItem> extends BaseListAdapter<Model, BaseViewHolder<Model, ItemSelectionBinding>> {
    private final OnItemClickListener<Model> onItemSelected;

    public ItemSelectionListAdapter(Context context, OnItemClickListener<Model> onItemSelected) {
        super(context);
        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @NotNull
    @Override
    public BaseViewHolder<Model, ItemSelectionBinding> onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new ItemSelectionViewHolder(
                inflate(R.layout.item_selection, parent, false)
        );
    }

    public class ItemSelectionViewHolder extends BaseViewHolder<Model, ItemSelectionBinding> {

        public ItemSelectionViewHolder(@NonNull @NotNull ItemSelectionBinding itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Model item, int position) {
            binding.setItem(item);

            binding.getRoot().setOnClickListener(view ->
                    onItemSelected.onClick(item, position)
            );
        }
    }
}