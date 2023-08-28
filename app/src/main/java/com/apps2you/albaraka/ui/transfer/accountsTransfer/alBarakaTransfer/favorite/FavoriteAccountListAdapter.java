package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite;

import android.content.Context;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.databinding.ItemFavoriteAccountBinding;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.base.adapter.list.BaseListAdapter;
import com.apps2you.albaraka.ui.base.adapter.list.BaseViewHolder;

import org.jetbrains.annotations.NotNull;

public class FavoriteAccountListAdapter extends BaseListAdapter<FavoriteAccount, FavoriteAccountListAdapter.FavoriteAccountViewHolder> {
    private final OnItemClickListener<FavoriteAccount> onFavoriteAccountSelected;
    private final OnItemClickListener<FavoriteAccount> onFavoriteDeleteClicked;
    private final OnItemClickListener<FavoriteAccount> onFavoriteEditClicked;

    public FavoriteAccountListAdapter(Context context,
                                      OnItemClickListener<FavoriteAccount> onFavoriteAccountSelected,
                                      OnItemClickListener<FavoriteAccount> onFavoriteDeleteClicked,
                                      OnItemClickListener<FavoriteAccount> onFavoriteEditClicked) {
        super(context);
        this.onFavoriteAccountSelected = onFavoriteAccountSelected;
        this.onFavoriteDeleteClicked = onFavoriteDeleteClicked;
        this.onFavoriteEditClicked = onFavoriteEditClicked;
    }

    @NonNull
    @NotNull
    @Override
    public FavoriteAccountViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        return new FavoriteAccountViewHolder(
                inflate(R.layout.item_favorite_account, parent, false)
        );
    }

    public class FavoriteAccountViewHolder extends BaseViewHolder<FavoriteAccount, ItemFavoriteAccountBinding> {

        public FavoriteAccountViewHolder(@NonNull @NotNull ItemFavoriteAccountBinding itemView) {
            super(itemView);
        }

        @Override
        public void onBind(FavoriteAccount item, int position) {
            binding.setFavorite(item);

            binding.getRoot().setOnClickListener(view -> onFavoriteAccountSelected.onClick(item, position));
            binding.buttonDelete.setOnClickListener(view -> onFavoriteDeleteClicked.onClick(item, position));
            binding.buttonEdit.setOnClickListener(view -> onFavoriteEditClicked.onClick(item, position));
        }
    }
}
