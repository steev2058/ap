package com.apps2you.albaraka.ui.common.dialogs;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.DialogConfirmPinBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.MVVMFragmentDialog;
import com.apps2you.albaraka.utils.AsteriskPasswordTransformationMethod;
import com.apps2you.albaraka.viewmodels.ConfirmPinViewModel;

import org.jetbrains.annotations.NotNull;

public class ConfirmPinDialog extends MVVMFragmentDialog<ConfirmPinViewModel, DialogConfirmPinBinding> {
    public static final String TAG_CONFIRM_PIN_DIALOG = "confirm_pin_dialog";

    private final PinConfirmationListener pinConfirmationListener;

    private ConfirmPinDialog(PinConfirmationListener pinConfirmationListener) {
        this.pinConfirmationListener = pinConfirmationListener;
    }

    public static ConfirmPinDialog create(PinConfirmationListener pinConfirmationListener) {
        return new ConfirmPinDialog(pinConfirmationListener);
    }

    public static void show(FragmentManager fragmentManager, PinConfirmationListener pinConfirmationListener) {
        create(pinConfirmationListener).show(fragmentManager, TAG_CONFIRM_PIN_DIALOG);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCanceledOnTouchOutside(false);
        registerObservers();

        binding.pinView.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        binding.pinView.requestFocus();
        showKeyBoard();
    }

    private void registerObservers() {
        viewModel.pinCodeError.observe(getViewLifecycleOwner(), result -> {
            if (result)
                shakeError();
        });

        viewModel.checkPinStatus.observe(getViewLifecycleOwner(), result -> {
            if (result) {
                pinConfirmationListener.onPinConfirmed(viewModel.pinCode.getValue());
                dismiss();
            }
        });
    }

    private void shakeError() {
        Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake);
        binding.pinView.startAnimation(shake);
    }

    @Override
    protected Class<ConfirmPinViewModel> getViewModelClass() {
        return ConfirmPinViewModel.class;
    }

    @Override
    protected int getViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm_pin;
    }

    public interface PinConfirmationListener {
        void onPinConfirmed(String pinCode);
    }
}
