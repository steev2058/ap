package com.apps2you.albaraka.ui.common.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.ItemAccountSpinnerBinding;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.base.adapter.list.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class AccountsRecyclerAdapter extends BaseListAdapter<Account, AccountsRecyclerAdapter.AccountsViewHolder> {
    private final OnItemClickListener<Account> onAccountClickListener;

    public AccountsRecyclerAdapter(Context context, OnItemClickListener<Account> onAccountClickListener) {
        super(context);
        this.onAccountClickListener = onAccountClickListener;
    }

    @NonNull
    @NotNull
    @Override
    public AccountsViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        ItemAccountSpinnerBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.item_account_spinner,
                parent,
                false);
        return new AccountsViewHolder(binding);
    }

    public class AccountsViewHolder extends BaseViewHolder<Account, ItemAccountSpinnerBinding> {
        public AccountsViewHolder(@NonNull @NotNull ItemAccountSpinnerBinding itemView) {
            super(itemView);
        }

        @Override
        public void onBind(Account item, int position) {
            binding.setItem(item);

            binding.getRoot().setOnClickListener(view ->
                    onAccountClickListener.onClick(item, position)
            );
        }
    }
}
