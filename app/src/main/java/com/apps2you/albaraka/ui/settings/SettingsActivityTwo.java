package com.apps2you.albaraka.ui.settings;

import android.os.Build;
import android.view.MenuItem;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.NavGraph;
import androidx.navigation.Navigation;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.ActivitySettingsBinding;
import com.apps2you.albaraka.databinding.ActivitySettingstwoBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.LoginViewModel;



public class SettingsActivityTwo extends BaseActivity<ActivitySettingstwoBinding, LoginViewModel> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_settingstwo;
    }

    @Override
    public Class<LoginViewModel> setViewModel() {
        return LoginViewModel.class;
    }

    @Override
    public void setUpView() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavGraph navGraph = navController.getNavInflater().inflate(R.navigation.nav_settings);

        int destinationFragment = getIntent().getIntExtra("navigation_type", Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PIN);
        switch (destinationFragment) {
            case Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PIN:
                setToolbarTitle(getString(R.string.pin_title));
                navGraph.setStartDestination(R.id.resetPinFragment);
                navController.setGraph(navGraph);
                break;

            case Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PASS:
                setToolbarTitle(getString(R.string.password_title));
                navGraph.setStartDestination(R.id.resetPassFragment);
                navController.setGraph(navGraph);
                break;
        }


        getViewDataBinding().btnChangePin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToResetPinFragment();
            }
        });

        getViewDataBinding().btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToResetPassFragment();
            }
        });

    }
    // Method to navigate to reset pin fragment
    private void navigateToResetPinFragment() {
        setToolbarTitle(getString(R.string.pin_title));
        Navigation.findNavController(this, R.id.fragment)
                .navigate(R.id.resetPinFragment);

    }

    // Method to navigate to reset password fragment
    private void navigateToResetPassFragment() {
        setToolbarTitle(getString(R.string.password_title));
        Navigation.findNavController(this, R.id.fragment)
                .navigate(R.id.resetPassFragment);
    }


    public void updateBiometryPassword() {
        String newCIF = UserUtils.getInstance(this).getCIFNumber();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                canAuthBio()) {
            UserUtils.getInstance(this)
                    .saveCIF4AskedUserBiometricUsage(newCIF);
            clearPasswordFromStorage();

            confirmationDialog(getString(R.string.biometric_id),
                    getString(R.string.biometric_confirmation),
                    Constants.BIOMETRIC_AUTH_ACTION_TYPE);
        } else {
            finish();
        }
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }

    public void setToolbarTitle(String title) {
        setToolbarTitle(getViewDataBinding().toolbar, title);
    }

    @Override
    public void onBackPressed() {
        NavController navController = Navigation.findNavController(this, R.id.fragment);
        if (navController.getCurrentDestination() != null && navController.getCurrentDestination().getId() == R.id.OTPCodeFragment)//change the title if we get back from otpFragment to ResetPinFragment
            setToolbarTitle(getString(R.string.pin_title));

        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            finish();
        }
    }
}