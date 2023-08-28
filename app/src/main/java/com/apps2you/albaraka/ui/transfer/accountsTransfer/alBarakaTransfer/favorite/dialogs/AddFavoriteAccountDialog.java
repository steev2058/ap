package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps2you.albaraka.utils.lifecyle.EventObserver;

import org.jetbrains.annotations.NotNull;

public class AddFavoriteAccountDialog extends BaseFavoriteAccountDialog {

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerObservers();
    }

    @Override
    protected void checkArgs(){
        if (getArguments() != null){
            String cif = AddFavoriteAccountDialogArgs.fromBundle(getArguments()).getCIF();
            String gsm = AddFavoriteAccountDialogArgs.fromBundle(getArguments()).getGSM();

            if (cif != null) viewModel.addFavoriteAccountForm.accountCIF.setValue(cif);
            if (gsm != null) viewModel.addFavoriteAccountForm.accountGSM.setValue(gsm);
        }
    }

    @Override
    protected void submit() {
        viewModel.addFavoriteAccount();
    }

    private void registerObservers() {
        viewModel.addFavoriteAccountStatus.observe(getViewLifecycleOwner(), new EventObserver<>(result -> {
            if (result) {
                dismiss();
            }
        }));
    }

}
