package com.apps2you.albaraka.ui.registration;


import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentResetPasswordBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.settings.SettingsActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.utils.cryptography.ConstantsKt;
import com.apps2you.albaraka.viewmodels.LoginViewModel;

import java.util.regex.Pattern;


/**
 * A simple {@link Fragment} subclass.
 */
public class ResetPasswordFragment extends BaseFragment<FragmentResetPasswordBinding, LoginViewModel> implements CryptPasswordCallback {

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
        return R.layout.fragment_reset_password;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
        if (mActivity instanceof SettingsActivity)//if we came from settings, then hide the title from the top of the fragment
            mViewDataBinding.resetPasswordTextView.setVisibility(View.GONE);

        mViewDataBinding.etCurrentPassword.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.currentPassInputLayout));
        mViewDataBinding.etNewPassword.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.inputLayoutNewPass));
        mViewDataBinding.etConfirmNewPassword.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.inputLayoutConfirmPass));


        mViewDataBinding.submitButton.setOnClickListener(v -> {

            if (validateFields(mViewDataBinding.etCurrentPassword.getText().toString(),
                    mViewDataBinding.etNewPassword.getText().toString(),
                    mViewDataBinding.etConfirmNewPassword.getText().toString())) {
                ConstantsKt.setUSER_PASS(mViewDataBinding.etNewPassword.getText().toString());
                resetPassword();
            }

        });
    }

    private boolean validateFields(String currentPassword, String newPassword, String confirmPassword) {
        if (currentPassword.isEmpty()) {
            setInputError(mViewDataBinding.currentPassInputLayout, getString(R.string.error_required));
            return false;
        } else if (newPassword.isEmpty()) {
            setInputError(mViewDataBinding.inputLayoutNewPass, getString(R.string.error_required));
            return false;
        } else if (!isPasswordValid(newPassword)) {
            setInputError(mViewDataBinding.inputLayoutNewPass, getString(R.string.password_validation_error));
            return false;
        } else if (confirmPassword.isEmpty()) {
            setInputError(mViewDataBinding.inputLayoutConfirmPass, getString(R.string.error_required));
            return false;
        } else if (!confirmPassword.equals(newPassword)) {
            setInputError(mViewDataBinding.inputLayoutConfirmPass, getString(R.string.password_error_match));
            return false;
        }
        return true;
    }

    public boolean isPasswordValid(String pass) {
        boolean isContainsLawerCharacter = Pattern.compile("[a-z]").matcher(pass).find();
        boolean isContainsUpperCharacter = Pattern.compile("[A-Z]").matcher(pass).find();
        boolean isContainsDigit = Pattern.compile("[0-9]").matcher(pass).find();
        boolean isContainsSpecialCharacter = Pattern.compile("[@#$%^&+=]").matcher(pass).find();

        if (TextUtils.isEmpty(pass) || pass.length() < 8 || !isContainsLawerCharacter || !isContainsUpperCharacter || !isContainsDigit || !isContainsSpecialCharacter)
            return false;
        return true;
    }

    private void resetPassword() {
        getViewModel().resetPassword(mViewDataBinding.etCurrentPassword.getText().toString(),
                mViewDataBinding.etNewPassword.getText().toString(),
                mViewDataBinding.etConfirmNewPassword.getText().toString()
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
                    User user = UserUtils.getInstance(getContext()).getUser();
                    user.setPassword(mViewDataBinding.etNewPassword.getText().toString());
                    UserUtils.getInstance(getContext()).saveUser(user);

                    if (mActivity instanceof LoginActivity) {
                        if (shouldChangeBinCode()) {
                            NavHostFragment.findNavController(this).navigate(R.id.action_resetPassFragment_to_resetPinFragment);
                        } else {
                            UserUtils.getInstance(getContext()).saveCIFNumber(user.getCif_number()); //save cif number so we can show it in next login tries(will be used as a local indicator if this is the first login try or not)
                            ((LoginActivity) mActivity).performLogin();
                        }
                    } else if (mActivity instanceof SettingsActivity) {
                        mActivity.setCryptPasswordCallback(this);
                        ((SettingsActivity) mActivity).updateBiometryPassword();
                    }
                    break;
            }
        });
    }

    private boolean shouldChangeBinCode() {
        Bundle arguments = getArguments();
        if (arguments != null && arguments.containsKey(Constants.ARG_CHANGE_PIN_CODE)) {
            return arguments.getBoolean(Constants.ARG_CHANGE_PIN_CODE, false);
        }
        return false;
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void encryptSuccess() {
        mActivity.finish();
    }

    @Override
    public void decryptSuccess(String password) {

    }

    @Override
    public void keyInvalidated() {

    }
}
