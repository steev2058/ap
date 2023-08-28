package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.data.model.FavoriteAccount;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;

import org.jetbrains.annotations.NotNull;

public class UpdateFavoriteAccountDialog extends BaseFavoriteAccountDialog {

    private FavoriteAccount favoriteAccount;

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerObservers();
    }

    private void registerObservers() {
        viewModel.updateFavoriteAccountStatus.observe(getViewLifecycleOwner(), new EventObserver<>(
                result -> {
                    if (result) {
                        hideKeyboard();
                        dismiss();
                    }
                })
        );
    }

    @Override
    protected void checkArgs() {
        if (getArguments() != null) {
            favoriteAccount = UpdateFavoriteAccountDialogArgs.fromBundle(getArguments()).getFavoriteAccount();
            updateInput();
        }
    }

    private void updateInput() {
        AddFavoriteAccountForm addFavoriteAccountForm = viewModel.addFavoriteAccountForm;
        if (favoriteAccount != null) {
            addFavoriteAccountForm.accountName.setValue(favoriteAccount.getName());

            if (favoriteAccount.getCIF() != null)
                addFavoriteAccountForm.accountCIF.setValue(favoriteAccount.getCIF());

            if (favoriteAccount.getGSM() != null)
                addFavoriteAccountForm.accountGSM.setValue(favoriteAccount.getGSM());
        }
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected void submit() {
        viewModel.updateFavoriteAccount(favoriteAccount);
    }
}
