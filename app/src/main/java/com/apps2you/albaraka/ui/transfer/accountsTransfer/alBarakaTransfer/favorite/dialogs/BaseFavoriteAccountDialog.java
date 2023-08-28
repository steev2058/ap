package com.apps2you.albaraka.ui.transfer.accountsTransfer.alBarakaTransfer.favorite.dialogs;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.DialogAddFavoriteAccountBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.MVVMFragmentDialog;
import com.apps2you.albaraka.viewmodels.transfer.FavoriteAccountsViewModel;

import org.jetbrains.annotations.NotNull;

public abstract class BaseFavoriteAccountDialog extends MVVMFragmentDialog<FavoriteAccountsViewModel, DialogAddFavoriteAccountBinding> {

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCanceledOnTouchOutside(false);
        checkArgs();
        showKeyBoard();
        setUpListeners();
    }

    private void setUpListeners() {
        binding.buttonSave.setOnClickListener(view -> validateInput());
    }

    protected void validateInput(){
        AddFavoriteAccountForm form = viewModel.addFavoriteAccountForm;

        if (TextUtils.isEmpty(form.accountName.getValue())) {
            showToast(R.string.error_fields_required);
            binding.editTextAccountName.requestFocus();
        } else if (TextUtils.isEmpty(form.accountCIF.getValue())
                && TextUtils.isEmpty(form.accountGSM.getValue())) {
            showToast(R.string.you_should_enter_either_cif_or_gsm);
        } else {
            submit();
        }
    }

    protected abstract void checkArgs();
    protected abstract void submit();

    @Override
    protected Class<FavoriteAccountsViewModel> getViewModelClass() {
        return FavoriteAccountsViewModel.class;
    }

    @Override
    protected int getViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_add_favorite_account;
    }
}
