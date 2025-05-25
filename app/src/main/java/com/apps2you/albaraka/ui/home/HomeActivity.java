package com.apps2you.albaraka.ui.home;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.MyDevices;
import com.apps2you.albaraka.data.model.NotificationContent;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.data.remote.networkUtils.Status;
import com.apps2you.albaraka.databinding.ActivityHomeBinding;
import com.apps2you.albaraka.databinding.DialogLanguageBinding;
import com.apps2you.albaraka.ui.PrivacyPolicyActivity;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.devices.MyDevicesActivity;
import com.apps2you.albaraka.ui.locations.LocationsActivity;
import com.apps2you.albaraka.ui.notifications.NotificationsActivity;
import com.apps2you.albaraka.ui.registration.CryptPasswordCallback;
import com.apps2you.albaraka.ui.registration.LoginActivity;
import com.apps2you.albaraka.ui.settings.SettingsActivity;
import com.apps2you.albaraka.ui.settings.SettingsActivityTwo;
import com.apps2you.albaraka.ui.transactions.TransactionDetailsActivity;
import com.apps2you.albaraka.ui.transfer.qrPayment.QrPaymentActivity;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.HomeViewModel;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.messaging.FirebaseMessaging;

import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.NOTIFICATION_EXTRA;

import io.reactivex.annotations.NonNull;

public class HomeActivity extends BaseActivity<ActivityHomeBinding, HomeViewModel> implements CryptPasswordCallback {

    BottomSheetBehavior<RelativeLayout> bottomSheetBehavior;

    CompoundButton.OnCheckedChangeListener touchIDListener = (buttonView, isChecked) -> {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                canAuthBio()) {
            if (isChecked) {
                showBiometricPromptForEncryption();
            } else {
                clearPasswordFromStorage();
            }
        } else {
            mViewModel.showTouchIDOption.set(canAuthBio());
        }
    };

    CompoundButton.OnCheckedChangeListener notificationListener = (buttonView, isChecked) -> {
        if (isChecked) {
            turnNotifications(1);
        } else {
            turnNotifications(0);
        }
    };

    CompoundButton.OnCheckedChangeListener otpListener = (buttonView, isChecked) -> {
        if (isChecked) {
            turnOtp(1);
        } else {
            turnOtp(0);
        }
    };

    public void toggleDarkMode(boolean isDarkMode) {
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        mViewDataBinding.bottomSheet.switchTouchId.setOnCheckedChangeListener(null);
        mViewDataBinding.bottomSheet.switchTouchId.setChecked(canUseBiometric());
        mViewDataBinding.bottomSheet.switchTouchId.setOnCheckedChangeListener(touchIDListener);

        //notifications
        mViewDataBinding.bottomSheet.switchNotification.setOnCheckedChangeListener(null);
        mViewDataBinding.bottomSheet.switchNotification.setChecked(mViewModel.getUser()
                .getEnableNotifications() == 1);
        mViewDataBinding.bottomSheet.switchNotification.setOnCheckedChangeListener(notificationListener);
        mViewDataBinding.bottomSheet.switchNotification.setOnCheckedChangeListener(notificationListener);

        //otp
        mViewDataBinding.bottomSheet.switchOtp.setOnCheckedChangeListener(null);
        mViewDataBinding.bottomSheet.switchOtp.setChecked(mViewModel.getUser()
                .getEnableOtp() == 1);
        mViewDataBinding.bottomSheet.switchOtp.setOnCheckedChangeListener(otpListener);
        mViewDataBinding.bottomSheet.switchOtp.setOnCheckedChangeListener(otpListener);


        SwitchCompat darkModeSwitch = findViewById(R.id.switch_dark_mode);
        darkModeSwitch.setChecked(isDarkThemeEnabled());

        darkModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            toggleDarkMode(isChecked);
            // optional: store in shared preferences
        });

    }
    private boolean isDarkThemeEnabled() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        return nightMode == AppCompatDelegate.MODE_NIGHT_YES ||
                (nightMode == AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM &&
                        (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    public Class<HomeViewModel> setViewModel() {
        return HomeViewModel.class;
    }

    @Override
    public void setUpView() {
        mViewDataBinding.iBtnLogout.setOnClickListener(view -> logoutConfirmation());

        mViewDataBinding.iBtnMore.setOnClickListener(view -> {
//            startActivity(new Intent(HomeActivity.this, MoreActivity.class));
            mViewModel.showTouchIDOption.set(canAuthBio());
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        });

        FloatingActionButton fabChatBot = findViewById(R.id.fab_chatbot);
        fabChatBot.setOnClickListener(view -> {
            new ChatBotDialogFragment().show(getSupportFragmentManager(), "ChatBotDialog");
        });
        mViewDataBinding.bottomSheet.switchDarkMode.setChecked(isDarkThemeEnabled());
        mViewDataBinding.bottomSheet.switchDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
            editor.putBoolean("dark_mode", isChecked).apply();
            toggleDarkMode(isChecked);
        });




        mViewDataBinding.iBtnNotifications.setOnClickListener(v -> startActivity(new Intent(this, NotificationsActivity.class)));

        mViewDataBinding.iBtnLocations.setOnClickListener(v -> startActivity(new Intent(this, LocationsActivity.class)));

        mViewDataBinding.iBtnScan.setOnClickListener(v -> startActivity(new Intent(this, QrPaymentActivity.class)));

        NavController navController = Navigation.findNavController(this, R.id.fragment);
        NavigationUI.setupWithNavController(mViewDataBinding.bottomNavigationView, navController);

        //bottom sheet setup
        mViewDataBinding.bottomSheet.setViewModel(mViewModel);
        mViewDataBinding.bottomSheet.executePendingBindings();
        bottomSheetBehavior = BottomSheetBehavior.from(mViewDataBinding.bottomSheet.bottomSheetLayout);
        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_EXPANDED) {
                    fabChatBot.hide();
                } else {
                    fabChatBot.show();
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {}
        });
        mViewDataBinding.bottomSheet.tvPrivacy.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, PrivacyPolicyActivity.class)));
        mViewDataBinding.bottomSheet.tvMyDevices.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, MyDevicesActivity.class)));
        mViewDataBinding.bottomSheet.tvLogout.setOnClickListener(v -> logoutConfirmation());
        mViewDataBinding.bottomSheet.closeBtn.setOnClickListener(v -> bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN));
        mViewDataBinding.bottomSheet.tvChangeLanguage.setOnClickListener(v -> languageDialog());

        mViewDataBinding.bottomSheet.tvChangePin.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            intent.putExtra("navigation_type", Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PIN);
            startActivity(intent);
        });

        mViewDataBinding.bottomSheet.tvChangePass.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivity.class);
            intent.putExtra("navigation_type", Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PASS);
            startActivity(intent);
        });



        mViewDataBinding.bottomSheet.tvChangePin.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivityTwo.class);
            intent.putExtra("navigation_type", Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PIN);
            startActivity(intent);
        });

        mViewDataBinding.bottomSheet.tvChangePass.setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, SettingsActivityTwo.class);
            intent.putExtra("navigation_type", Constants.NAVIGATION_FROM_SETTINGS_TO_RESET_PASS);
            startActivity(intent);
        });

        //notification setup
        NotificationContent notification;
        if ((notification = (NotificationContent) getIntent()
                .getSerializableExtra(NOTIFICATION_EXTRA)) != null) {
            resetNotificationCount();

            if (notification.getType() == Constants.NOTIFICATION_TRANSFER) {
                startActivity(TransactionDetailsActivity.getIntent(this, notification.getOriginalTransactionId(), notification.getBranchCode()));
            } else if (notification.getType() == Constants.NOTIFICATION_NORMAL) {
                confirmationDialog(notification.getTitle(), notification.getText(), Constants.NOTIFICATIONS_ACTION_TYPE);
            }
        } else {
            HomeViewModel.notificationCount.set(mViewModel.getUser().getNotificationCounter());
        }

        MyApplication.isLoggedIn = true;

        checkVisitor();
    }

    private void checkVisitor(){
        User user = UserUtils.getInstance(this).getUser();
        if (user.isHideServices()){
            mViewDataBinding.bottomNavigationView.getMenu().removeItem(R.id.MoreFragment);
        }
    }

    private void logoutConfirmation() {
        confirmationDialog(getString(R.string.logout_title), getString(R.string.logout_confirmation), Constants.LOGOUT_CONFIRMATION_ACTION_TYPE);
    }

    private void languageDialog() {
        final Dialog dialog = new Dialog(this);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogLanguageBinding dialogDataBinding = DialogLanguageBinding.inflate(LayoutInflater.from(this),
                null,
                false);
        dialog.setContentView(dialogDataBinding.getRoot());

        String localLang = UserUtils.getInstance(this).getLanguage();
        if (localLang.equalsIgnoreCase("ar")) {
            dialogDataBinding.rgLanguage.check(dialogDataBinding.rbArabic.getId());
        } else {
            dialogDataBinding.rgLanguage.check(dialogDataBinding.rbEnglish.getId());
        }
        dialog.show();

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            getViewModel().lang = "en";
            if (dialogDataBinding.rgLanguage.getCheckedRadioButtonId() ==
                    dialogDataBinding.rbArabic.getId()) {
                getViewModel().lang = "ar";
            }

            if (!localLang.equalsIgnoreCase(getViewModel().lang)) {
                performAction(Constants.LANGUAGE_SELECTION_ACTION_TYPE);
            }

        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> {
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
            performCancelAction(Constants.LANGUAGE_SELECTION_ACTION_TYPE);
        });


    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {
        setCryptPasswordCallback(this);
    }

    @Override
    protected void performAction(int action_type) {
        if (action_type == Constants.LOGOUT_CONFIRMATION_ACTION_TYPE) {
            logout();
        } else if (action_type == Constants.LANGUAGE_SELECTION_ACTION_TYPE) {
            changeLanguage();
        }
    }

    private void logout() {
//        getViewModel().logout().observe(this, resource -> {
//            switch (resource.status) {
//                case LOADING:
//                    showProgress();
//                    break;
//
//                case ERROR:
//                    hideProgress();
//                    showToast(resource.getMessage());
//                    break;
//
//                case SUCCESS:
//                    hideProgress();
        UserUtils.getInstance(this).clearUser();
        MyApplication.isLoggedIn = false;
        startActivity(new Intent(this, LoginActivity.class));
        finish();
//
//                    break;
//            }
//        });
    }

    private void changeLanguage() {
        getViewModel().changeLanguage(getViewModel().lang).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    if (!isFinishing() && !isDestroyed()) {
                    showProgress();
                    }
                    break;

                case ERROR:
                    hideProgress();
                    showToast(resource.getMessage());
                    break;

                case SUCCESS:
                    hideProgress();
                    UserUtils.getInstance(this).setLanguage(getViewModel().lang);
//                    Intent homeIntent = new Intent(this, HomeActivity.class);
//                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(homeIntent);

                    Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage(getBaseContext().getPackageName());
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    UserUtils.getInstance(this).setShouldRedirectToHome(true);
                    startActivity(i);
                    break;
            }
        });
    }

    private void resetNotificationCount() {
        getViewModel().resetNotificationCounter().observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    break;

                case ERROR:
                    showToast(resource.getMessage());
                    break;

                case SUCCESS:
                    HomeViewModel.notificationCount.set(HomeViewModel.notificationCount.get() - 1);
                    break;
            }
        });
    }

    private void turnNotifications(int status) {
        getViewModel().turnNotifications(status).observe(this, resource -> {
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
                    mViewModel.getUser().setEnableNotifications(status);
                    UserUtils.getInstance(this).saveUser(mViewModel.getUser());
                    break;
            }
            mViewDataBinding.bottomSheet.switchNotification.setOnCheckedChangeListener(null);
            mViewDataBinding.bottomSheet.switchNotification.setChecked(mViewModel.getUser()
                    .getEnableNotifications() == 1);
            mViewDataBinding.bottomSheet.switchNotification.setOnCheckedChangeListener(notificationListener);
            if (resource.status != Status.LOADING && mViewModel.getUser().getEnableNotifications() == 1) {
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC);
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC);
            }
        });
    }

    private void turnOtp(int status) {
        getViewModel().turnOtp(status).observe(this, resource -> {
            switch (resource.status) {
                case LOADING:
                    if (!isFinishing() && !isDestroyed()) {
                        showProgress();
                    }
                    break;

                case ERROR:
                    hideProgress();
                    showToast(resource.getMessage());
                    break;

                case SUCCESS:
                    hideProgress();
                    mViewModel.getUser().setEnableOtp(status);
                    UserUtils.getInstance(this).saveUser(mViewModel.getUser());
                    break;
            }
            mViewDataBinding.bottomSheet.switchOtp.setOnCheckedChangeListener(null);
            mViewDataBinding.bottomSheet.switchOtp.setChecked(mViewModel.getUser()
                    .getEnableOtp() == 1);
            mViewDataBinding.bottomSheet.switchOtp.setOnCheckedChangeListener(otpListener);
            if (resource.status != Status.LOADING && mViewModel.getUser().getEnableOtp() == 1) {
                FirebaseMessaging.getInstance().subscribeToTopic(Constants.FCM_TOPIC);
            } else {
                FirebaseMessaging.getInstance().unsubscribeFromTopic(Constants.FCM_TOPIC);
            }
        });
    }

    @Override
    public void encryptSuccess() {
        mViewDataBinding.bottomSheet.switchTouchId.setOnCheckedChangeListener(null);
        mViewDataBinding.bottomSheet.switchTouchId.setChecked(canUseBiometric());
        mViewDataBinding.bottomSheet.switchTouchId.setOnCheckedChangeListener(touchIDListener);
    }

    @Override
    public void decryptSuccess(String password) {
    }

    @Override
    public void keyInvalidated() {
    }

    @Override
    public void onBackPressed() {
        if (bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            super.onBackPressed();
    }
}
