package com.apps2you.albaraka.ui.registration;

import android.content.Intent;
import android.os.Build;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.NotificationContent;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.ActivityLoginBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.LoginViewModel;

import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.NOTIFICATION_EXTRA;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
//        showLoginFragment();

        //notification setup
        NotificationContent notification;
        if ((notification = (NotificationContent) getIntent()
                .getSerializableExtra(NOTIFICATION_EXTRA)) != null) {
            if (MyApplication.isLoggedIn) {
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra(NOTIFICATION_EXTRA, notification);
                startActivity(intent);
                finish();
            } else {
                if (notification.getType() == Constants.NOTIFICATION_NORMAL) {
                    confirmationDialog(notification.getTitle(), notification.getText(), Constants.NOTIFICATIONS_ACTION_TYPE);
                }
            }
        }
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }

    public void performLogin() {
        //Biometric ID
        String cif4AskedUser = UserUtils.getInstance(this)
                .getCIF4AskedUserBiometricUsage();
        String newCIF = UserUtils.getInstance(this).getCIFNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                canAuthBio() &&
                !cif4AskedUser.equals(newCIF)) {
            UserUtils.getInstance(this)
                    .saveCIF4AskedUserBiometricUsage(newCIF);
            clearPasswordFromStorage();

            confirmationDialog(getString(R.string.biometric_id),
                    getString(R.string.biometric_confirmation),
                    Constants.BIOMETRIC_AUTH_ACTION_TYPE);
        } else {
            openHome();
        }
    }

    public void showBiometricRequest() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            showBiometricPromptForDecryption();
        }
    }

    public void openHome() {
        startActivity(new Intent(this, HomeActivity.class));
        finish();
    }

    @Override
    protected void performAction(int action_type) {
        if (action_type == Constants.BIOMETRIC_AUTH_ACTION_TYPE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                showBiometricPromptForEncryption();
            }
        }
    }

    @Override
    protected void performCancelAction(int action_type) {
        if (action_type == Constants.BIOMETRIC_AUTH_ACTION_TYPE) {
            openHome();
        }
    }
}