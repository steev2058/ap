package com.apps2you.albaraka.ui.atmCard;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.AtmLimit;
import com.apps2you.albaraka.databinding.ItemSelectionLimitBinding;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.base.adapter.list.BaseViewHolder;

import org.jetbrains.annotations.NotNull;


public class LimitSelectionAdapter extends BaseListAdapter<AtmLimit, BaseViewHolder<AtmLimit, ItemSelectionLimitBinding>> {

    private final OnItemClickListener<AtmLimit> onItemSelected;

    public LimitSelectionAdapter(Context context, OnItemClickListener<AtmLimit> onItemSelected) {
        super(context);

        this.onItemSelected = onItemSelected;
    }

    @NonNull
    @Override
    public BaseViewHolder<AtmLimit, ItemSelectionLimitBinding> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ItemSelectionViewHolder(
                inflate(R.layout.item_selection_limit, parent, false)
        );
    }

    public class ItemSelectionViewHolder extends BaseViewHolder<AtmLimit, ItemSelectionLimitBinding> {

        public ItemSelectionViewHolder(@NonNull @NotNull ItemSelectionLimitBinding itemView) {
            super(itemView);
        }

        @Override
        public void onBind(AtmLimit item, int position) {
            binding.setItem(item);

            binding.getRoot().setOnClickListener(view ->
                    onItemSelected.onClick(item, position)
            );
        }
    }
}
