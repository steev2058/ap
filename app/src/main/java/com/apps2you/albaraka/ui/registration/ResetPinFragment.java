package com.apps2you.albaraka.ui.registration;


import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentResetPinBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.settings.SettingsActivity;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.viewmodels.LoginViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPinFragment extends BaseFragment<FragmentResetPinBinding, LoginViewModel> {

    @Override
    public void createViewModel() {
        mViewModel = new ViewModelProvider(mActivity).get(LoginViewModel.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_reset_pin;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
        if (mActivity instanceof SettingsActivity)//if we came from settings, then hide the title from the top of the fragment
            mViewDataBinding.resetPinTextView.setVisibility(View.GONE);

        mViewDataBinding.etCurrentPin.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.currentPinInputLayout));
        mViewDataBinding.etNewPin.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.inputLayoutNewPin));
        mViewDataBinding.etConfirmNewPin.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.inputLayoutConfirmPin));


        mViewDataBinding.submitButton.setOnClickListener(v -> {

            if (validateFields(mViewDataBinding.etCurrentPin.getText().toString(),
                    mViewDataBinding.etNewPin.getText().toString(),
                    mViewDataBinding.etConfirmNewPin.getText().toString()))
                resetPin();

        });
    }

    private boolean validateFields(String currentPin, String newPin, String confirmPin) {
        if (currentPin.isEmpty()) {
            setInputError(mViewDataBinding.currentPinInputLayout, getString(R.string.error_required));
            return false;
        } else if (newPin.isEmpty()) {
            setInputError(mViewDataBinding.inputLayoutNewPin, getString(R.string.error_required));
            return false;
        } else if (newPin.length() != 4) {
            setInputError(mViewDataBinding.inputLayoutNewPin, getString(R.string.pin_error_length));
            return false;
        } else if (confirmPin.isEmpty()) {
            setInputError(mViewDataBinding.inputLayoutConfirmPin, getString(R.string.error_required));
            return false;
        } else if (!confirmPin.equals(newPin)) {
            setInputError(mViewDataBinding.inputLayoutConfirmPin, getString(R.string.pin_error_match));
            return false;
        }
        return true;
    }

    private void resetPin() {
        getViewModel().resetPin(mViewDataBinding.etCurrentPin.getText().toString(),
                mViewDataBinding.etNewPin.getText().toString(),
                mViewDataBinding.etConfirmNewPin.getText().toString(), null
        ).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    showProgress();
                    break;

                case ERROR:
                    hideProgress();
                    showToast(resource.getMessage());
                    break;

                case SUCCESS:
                    hideProgress();
                    showToast(resource.message);
                    if (mActivity instanceof LoginActivity) {
                        UserUtils.getInstance(getContext()).saveCIFNumber(UserUtils.getInstance(getContext()).getUser().getCif_number());//save cif number so we can show it in next login tries(will be used as a local indicator if this is the first login try or not)
                        ((LoginActivity) mActivity).performLogin();
                    } else if (mActivity instanceof SettingsActivity)//if we came from settings, then changing pin code needs otp code confirmation
                    {
                        hideProgress();
                        showToast(resource.message);
                        mActivity.finish();
                    }
                    break;
            }
        });
    }


    @Override
    public void fetchData() {

    }

}