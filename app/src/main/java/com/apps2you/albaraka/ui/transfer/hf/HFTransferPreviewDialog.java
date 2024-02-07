package com.apps2you.albaraka.ui.transfer.hf;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.DialogConfirmHfPaymentBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.MVVMFragmentDialog;
import com.apps2you.albaraka.viewmodels.transfer.HFViewModel;

import org.jetbrains.annotations.NotNull;

public class HFTransferPreviewDialog extends MVVMFragmentDialog<HFViewModel, DialogConfirmHfPaymentBinding> {
    public static final String TAG_TRANSFER_PREVIEW_DIALOG = "transfer_preview_dialog";

    public static void show(FragmentManager fragmentManager) {
        new com.apps2you.albaraka.ui.transfer.hf.HFTransferPreviewDialog().show(fragmentManager, TAG_TRANSFER_PREVIEW_DIALOG);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.buttonCancel.setOnClickListener(v -> dismiss());
        binding.buttonConfirm.setOnClickListener(v -> {
            dismiss();
            viewModel.confirmPayment();
        });
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected Class<HFViewModel> getViewModelClass() {
        return HFViewModel.class;
    }

    @Override
    protected int getViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm_hf_payment;
    }
}
