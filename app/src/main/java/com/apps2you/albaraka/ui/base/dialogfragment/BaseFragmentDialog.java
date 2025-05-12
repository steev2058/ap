package com.apps2you.albaraka.ui.base.dialogfragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.fragment.app.DialogFragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.base.IBaseView;
import com.apps2you.albaraka.ui.base.alert.IDialogAlert;
import com.apps2you.albaraka.utils.BlurUtils;


public abstract class BaseFragmentDialog<DB extends ViewDataBinding> extends DialogFragment implements IBaseView {

    protected DB binding;

    protected NavController navController;

    protected abstract int getLayoutId();

    protected void setFullScreen() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    protected void setFullWidth() {
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }


    protected void showKeyBoard(){
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }
    /**
     * You should call this method in 'onViewCreated' method.
     */
    protected void setCanceledOnTouchOutside(boolean cancelable) {
        if (getDialog() != null)
            getDialog().setCanceledOnTouchOutside(cancelable);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        binding.setLifecycleOwner(requireActivity());
        binding.executePendingBindings();

        try {
            navController = NavHostFragment.findNavController(this);
        } catch (Exception ignored) {

        }

        return binding.getRoot();
    }

    public void blurBackground(){
        setFullScreen();
        requireView().setBackground(
                BlurUtils.getBlurredDrawable(requireActivity(), 30)
        );
    }

    @Override
    public IDialogAlert provideDialogAlert() {
        if (requireActivity() instanceof BaseActivity)
            return ((BaseActivity) requireActivity()).provideDialogAlert();
        return null;
    }

    public void showToast(final String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes final int stringRes) {
        showToast(getString(stringRes));
    }

}
