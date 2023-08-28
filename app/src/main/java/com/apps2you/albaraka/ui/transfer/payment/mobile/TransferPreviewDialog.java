package com.apps2you.albaraka.ui.transfer.payment.mobile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.DialogConfirmMobilePaymentBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.MVVMFragmentDialog;
import com.apps2you.albaraka.viewmodels.transfer.payment.MobilePaymentViewModel;

import org.jetbrains.annotations.NotNull;

public class TransferPreviewDialog extends MVVMFragmentDialog<MobilePaymentViewModel, DialogConfirmMobilePaymentBinding> {
    public static final String TAG_TRANSFER_PREVIEW_DIALOG = "transfer_preview_dialog";

    public static void show(FragmentManager fragmentManager) {
        new TransferPreviewDialog().show(fragmentManager, TAG_TRANSFER_PREVIEW_DIALOG);
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
        return getParentFragment();
    }

    @Override
    protected Class<MobilePaymentViewModel> getViewModelClass() {
        return MobilePaymentViewModel.class;
    }

    @Override
    protected int getViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm_mobile_payment;
    }
}
