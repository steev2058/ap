package com.apps2you.albaraka.ui.settings;


import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.FragmentOtpCodeBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.viewmodels.LoginViewModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class otpCodeFragment extends BaseFragment<FragmentOtpCodeBinding, LoginViewModel> {

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
        return R.layout.fragment_otp_code;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
        ((SettingsActivity) mActivity).setToolbarTitle(getString(R.string.verification));

        mViewDataBinding.etOtpCode.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.otpCodeInputLayout));


        mViewDataBinding.submitButton.setOnClickListener(v -> {
            if (mViewDataBinding.etOtpCode.getText().toString().isEmpty()) {
                setInputError(mViewDataBinding.otpCodeInputLayout, getString(R.string.error_required));
                return;
            }
            sendOtpCode(mViewDataBinding.etOtpCode.getText().toString());
        });

        mViewDataBinding.resendCodeTv.setOnClickListener(view -> sendOtpCode(null));
    }

    private void sendOtpCode(String code) {
        if (getArguments() != null)
            getViewModel().resetPin(getArguments().getString("old_pin"),
                    getArguments().getString("new_pin"),
                    getArguments().getString("confirm_new_pin"),
                    code
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
                        if (code != null) //we want to finish the activity only if the user entered the otp code (if code ==null so the user pressed resend code)
                            mActivity.finish();
                        break;
                }
            });
    }


    @Override
    public void fetchData() {

    }

}