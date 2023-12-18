package com.apps2you.albaraka.ui.registration;


import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.text.HtmlCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.FragmentKycBinding;
import com.apps2you.albaraka.databinding.FragmentLoginBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.ui.home.GuestHomeActivity;
import com.apps2you.albaraka.ui.kyc.KycActivity;
import com.apps2you.albaraka.ui.kyc.fragments.KycFragment;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.CustomTextWatcher;
import com.apps2you.albaraka.utils.cryptography.ConstantsKt;
import com.apps2you.albaraka.viewmodels.LoginViewModel;
import com.google.firebase.messaging.FirebaseMessaging;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends BaseFragment<FragmentLoginBinding, LoginViewModel> implements CryptPasswordCallback {

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
        return R.layout.fragment_login;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
//        mViewDataBinding.textInputLayout.setHintAnimationEnabled(false);
//        getViewModel().setNavigator(getActivity());

        mViewDataBinding.etCifNumber.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.inputCif));
        mViewDataBinding.etPassword.addTextChangedListener(new CustomTextWatcher(mViewDataBinding.textInputLayout));

        if (!UserUtils.getInstance(getContext()).isPrivacyAgreed()) // show privacy policy when user opens the app for the first time
            showPrivacyPolicy();
        else {
            checkVisitor();
        }

        String cifNumber = UserUtils.getInstance(getContext()).getCIFNumber();
        if (cifNumber != null) { //not first login
            mViewDataBinding.etCifNumber.setText(cifNumber);
            getViewModel().getUser().setCif_number(cifNumber);
            mViewDataBinding.textViewTitle.setVisibility(View.GONE);
        }

        if (mActivity.canUseBiometric()) {
            mViewDataBinding.fingerPrintText.setVisibility(View.VISIBLE);
            mViewDataBinding.ivFingerPrint.setVisibility(View.VISIBLE);
        }

        mViewDataBinding.submitButton.setOnClickListener(v -> {
            if (TextUtils.isEmpty(getViewModel().getUser().getCif_number()))
                setInputError(mViewDataBinding.inputCif, getString(R.string.error_required));
            else if (TextUtils.isEmpty(getViewModel().getUser().getPassword()))
                setInputError(mViewDataBinding.textInputLayout, getString(R.string.error_required));
            else {
                login();
            }
        });

//        mViewDataBinding.applicationSubscriptionRequest.setOnClickListener(view -> {
//            try {
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://albaraka.com.sy/KYC/"));
//                startActivity(intent);
//            }catch (Exception e) {
//                // Handle other exceptions
//                showToast("Error occurred");
//            }
//        });
        mViewDataBinding.applicationSubscriptionRequest.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(requireContext(), KycActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                // Handle other exceptions
                showToast("Error occurred");
            }
        });




        mViewDataBinding.requestToOpenAnAccount.setOnClickListener(view -> {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://albaraka.com.sy/AlBarakaForms/mobileForm"));
                startActivity(intent);
            }catch (Exception e) {
                // Handle other exceptions
                showToast("Error occurred");
            }
        });

        mViewDataBinding.ivFingerPrint.setOnClickListener(v -> {
            if (TextUtils.isEmpty(getViewModel().getUser().getCif_number())) {
                showToast(getString(R.string.please_provide_your_cif_number));
            } else {
                mActivity.setCryptPasswordCallback(this);
                ((LoginActivity) mActivity).showBiometricRequest();
            }
        });

    }

    @Override
    public void fetchData() {
    }

    private void login() {
        login(mViewModel.getUser().getPassword());
    }

    private void login(String password) {
        ConstantsKt.setUSER_PASS(password);
        String token = UserUtils.getInstance(getContext()).getFCMToken();
        getViewModel().login(password, token).observe(this, resource -> {
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
                    mActivity.setCryptPasswordCallback(this);
                    getViewModel().setUser(resource.data);
                    UserUtils.getInstance(getContext()).saveUser(resource.data);
                    UserUtils.getInstance(getContext()).setLanguage(resource.data.getClientLang());

                    //fcm topic
                    if (resource.data.getEnableNotifications() == 1) {
                        FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC);
                    } else {
                        FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC);
                    }

                    if (resource.data.shouldChangePassword()) {

                        //the user didn't change password before, so go to change password screen
                        Bundle args = new Bundle();
                        args.putBoolean(Constants.ARG_CHANGE_PIN_CODE, resource.data.shouldChangePinCode());
                        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_resetPassFragment, args);
                    } else if (resource.data.shouldChangePinCode()) {
                        //the user changed password before, but without changing pin code (didn't complete the first login flow) so go to change pin code screen
                        NavHostFragment.findNavController(this).navigate(R.id.action_loginFragment_to_resetPinFragment);
                    } else {//the user completed the login flow before, so go to home
                        UserUtils.getInstance(getContext()).saveCIFNumber(UserUtils.getInstance(getContext()).getUser().getCif_number());

                        ((LoginActivity) mActivity).performLogin();
                    }
                    break;
            }
        });
    }

    @Override
    public void encryptSuccess() {
        ((LoginActivity) mActivity).openHome();
    }

    @Override
    public void decryptSuccess(String password) {
        login(password);
    }

    @Override
    public void keyInvalidated() {
        showToast(getString(R.string.There_is_no_biometric_record_saved_for_the_entered));
        mViewDataBinding.fingerPrintText.setVisibility(View.GONE);
        mViewDataBinding.ivFingerPrint.setVisibility(View.GONE);
    }

    private void showPrivacyPolicy() {
        getViewModel().getPrivacyPolicy().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    showProgress();
                    break;

                case SUCCESS:
                    hideProgress();

                    Dialog dialog = new Dialog(getContext());

                    if (dialog.getWindow() != null) {
                        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
                    }

                    dialog.setContentView(R.layout.dialog_privacy);
                    dialog.setCancelable(false);

                    Button submitButton = dialog.findViewById(R.id.button_submit);

                    TextView textView = dialog.findViewById(R.id.textView);
                    textView.setText(HtmlCompat.fromHtml(resource.data.getContent(), HtmlCompat.FROM_HTML_MODE_LEGACY));

//                    ScrollView scrollView = dialog.findViewById(R.id.scroll_view);

//                    scrollView.getViewTreeObserver().addOnGlobalLayoutListener(() -> {
//                        submitButton.setEnabled(!scrollView.canScrollVertically(1));
//                    });

//                    scrollView.getViewTreeObserver()
//                            .addOnScrollChangedListener(() -> {
//                                submitButton.setEnabled(
//                                        !scrollView.canScrollVertically(1)
//                                );
//                            });
                    dialog.show();

                    submitButton.setOnClickListener(v -> {
                        dialog.dismiss();
                        UserUtils.getInstance(getContext()).setPrivacyAgreed();
                        checkVisitor();
                    });

                    dialog.findViewById(R.id.button_cancel).setOnClickListener(v -> {
                        dialog.dismiss();
                        requireActivity().finishAffinity();
                    });
                    break;

                default:
                    hideProgress();
                    showToast(resource.getMessage());
                    checkVisitor();
            }
        });
    }


    private void checkVisitor() {
        boolean isVisitorChecked = UserUtils.getInstance(requireContext()).isVisitorChecked();
        if (false) {
            setSkipVisibility(false);
            /*getViewModel().checkVisitor().observe(getViewLifecycleOwner(), resource -> {
                switch (resource.status) {
                    case LOADING:
                        showProgress();
                        break;

                    case SUCCESS:
                        hideProgress();
                        boolean updatedVisitorChecked = resource.data == null || resource.data;
                        UserUtils.getInstance(getContext()).setVisitorChecked(updatedVisitorChecked);
                        setSkipVisibility(!updatedVisitorChecked);
                        break;

                    default:
                        hideProgress();
                }
            });*/
        } else {
            setSkipVisibility(true);
        }
    }

    private void setSkipVisibility(boolean isVisible) {
        if (isVisible) {
            mViewDataBinding.requestToOpenAnAccount.setVisibility(View.VISIBLE);
            mViewDataBinding.applicationSubscriptionRequest.setVisibility(View.VISIBLE);
        } else {
            mViewDataBinding.applicationSubscriptionRequest.setVisibility(View.GONE);
            mViewDataBinding.requestToOpenAnAccount.setVisibility(View.GONE);
        }
    }
}
