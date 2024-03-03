package com.apps2you.albaraka.ui.base;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.LayoutRes;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;
import androidx.appcompat.widget.Toolbar;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.app.NavUtils;
import androidx.core.app.TaskStackBuilder;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.ViewModelProvider;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.DialogConfirmBinding;
import com.apps2you.albaraka.ui.base.alert.IDialogAlert;
import com.apps2you.albaraka.ui.common.busEvent.DismissRequestErrorEvent;
import com.apps2you.albaraka.ui.common.busEvent.UnAuthorizedUserEvent;
import com.apps2you.albaraka.ui.common.dialogs.DataRetrievalErrorDialog;
import com.apps2you.albaraka.ui.registration.CryptPasswordCallback;
import com.apps2you.albaraka.ui.registration.LoginActivity;
import com.apps2you.albaraka.utils.BindingUtils;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.bus.Bus;
import com.apps2you.albaraka.utils.bus.EventBusObserver;
import com.apps2you.albaraka.utils.cryptography.BiometricPromptUtils;
import com.apps2you.albaraka.utils.cryptography.CiphertextWrapper;
import com.apps2you.albaraka.utils.cryptography.ConstantsKt;
import com.apps2you.albaraka.utils.cryptography.CryptographyManager;
import com.apps2you.albaraka.utils.cryptography.CryptographyManagerImplKt;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.utils.navigation.ActivityNavigation;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.inject.Inject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dagger.android.AndroidInjection;
import dagger.android.support.DaggerAppCompatActivity;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;
import kotlin.Unit;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.CIPHERTEXT_WRAPPER;
import static com.apps2you.albaraka.utils.cryptography.ConstantsKt.SHARED_PREFS_FILENAME;


public abstract class BaseActivity<VB extends ViewDataBinding, VM extends BaseViewModel> extends DaggerAppCompatActivity implements IBaseView {
    private final int INACTIVITY_THRESHOLD = 5 * 60 * 1000; // in milliseconds

    public VB mViewDataBinding;
    public VM mViewModel;
    private SweetAlertDialog progressDialog;
    private CryptPasswordCallback cryptPasswordCallback;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    protected ActivityNavigation activityNavigator;

    private IDialogAlert dialogAlert;

    private boolean isRunning;

    // to detect user inactivity
    public static long lastUserInteraction = System.currentTimeMillis();

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected CryptographyManager cryptographyManager = CryptographyManagerImplKt.CryptographyManager();

    @Inject
    protected ViewModelProvider.Factory factory;

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    public abstract int getBindingVariable();

    /**
     * @return layout resource id
     */
    public abstract
    @LayoutRes
    int getLayoutId();

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    public abstract Class<VM> setViewModel();

    /**
     * set up view and any necessary
     * binding or setting any views
     * for fetching the data.
     */
    public abstract void setUpView();

    /**
     * fetch data
     */
    public abstract void fetchData();

    /**
     * listen to to variables (LiveData variables)
     * that will be updated with data,
     */
    public abstract void listenToVariables();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        performDependencyInjection();
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        activityNavigator =
                ActivityNavigation
                        .create(this)
                        .forResult(getActivityResultRegistry(), getLifecycle(), getClass().getName());

        setLanguage(BaseActivity.this);
        performDataBinding();


        initDialogAlert();
        setupBaseObservers();
        setBackButtonAction();
        setUpView();
        fetchData();
        listenToVariables();
        registerEvents();
    }

    protected void setupBaseObservers() {
        mViewModel.toastMessage.observe(this, new EventObserver<>(this::showToast));
        mViewModel.toastMessageResource.observe(this, new EventObserver<>(this::showToast));
        mViewModel.hideKeyboard.observe(this, new EventObserver<>(ignored -> hideKeyboard()));
        mViewModel.error().observe(this, this::showErrorDialog);
        mViewModel.isLoading().observe(this, isLoading -> {
            if (isLoading) {
                hideKeyboard();
                showProgress();
            } else {
                hideProgress();
            }
        }
        );
    }

    private void setBackButtonAction() {
        try {
            findViewById(R.id.back_button).setOnClickListener(v -> onToolbarBackPressed());
        } catch (Exception ignored) {
        }
    }

    protected void initDialogAlert() {
        dialogAlert = IDialogAlert.Factory.defaultAlert(this);
    }

    @Override
    public IDialogAlert provideDialogAlert() {
        return dialogAlert;
    }

    //fix app language if the app language is english and the user logged in with an Arabic account, to apply the Arabic font correctly
    @Override
    protected void attachBaseContext(Context newBase) {
        applyOverrideConfiguration(newBase.getApplicationContext().getResources().getConfiguration());
        super.attachBaseContext(ViewPumpContextWrapper.wrap(setLanguage(newBase)));
    }

    private void checkIfErrorDialogDisplayed() {
        if (getSupportFragmentManager().findFragmentByTag(DataRetrievalErrorDialog.TAG_ERROR_DIALOG) != null) {
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isRunning = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

        long currentUserInteraction = System.currentTimeMillis();
        boolean isInactive = (currentUserInteraction - lastUserInteraction) > INACTIVITY_THRESHOLD;
        if (isInactive && MyApplication.isLoggedIn) {
            onUnAuthorizedUserEvent();
        }
        lastUserInteraction = currentUserInteraction;
    }

    protected boolean isActivityRunning() {
        return isRunning;
    }

    private void registerEvents() {
        // Unauthorized user event
        registerUnAuthorizedUserEvent();

        // Dismiss request error event
        registerDismissRequestErrorEvent();
    }

    private void unregisterEvents() {
        unregisterUnAuthorizedUserEvent();
        unregisterDismissRequestErrorEvent();
    }

    private final EventBusObserver<UnAuthorizedUserEvent> unAuthorizedUserEventBusObserver = unAuthorizedUserEvent -> {
        if (isActivityRunning())
            onUnAuthorizedUserEvent();
    };

    protected void onUnAuthorizedUserEvent() {
        UserUtils.getInstance(getApplicationContext()).clearUser();
        MyApplication.isLoggedIn = false;
        ActivityNavigation.create(this).finishAffinity().navigate(LoginActivity.class);
        isRunning = false;
    }

    private void registerUnAuthorizedUserEvent() {
        Bus.instance().register(UnAuthorizedUserEvent.class, unAuthorizedUserEventBusObserver, true);
    }

    private void unregisterUnAuthorizedUserEvent() {
        Bus.instance().unregister(unAuthorizedUserEventBusObserver);
    }

    private final EventBusObserver<DismissRequestErrorEvent> dismissRequestErrorEventBusObserver = dismissRequestErrorEvent -> {
        if (dismissRequestErrorEvent.eventTrigger == this)
            onDismissRequestErrorEvent();
    };

    protected void onDismissRequestErrorEvent() {
        if (isActivityRunning())
            onToolbarBackPressed();
    }

    private void registerDismissRequestErrorEvent() {
        Bus.instance().register(DismissRequestErrorEvent.class, dismissRequestErrorEventBusObserver, true);
    }

    private void unregisterDismissRequestErrorEvent() {
        Bus.instance().unregister(dismissRequestErrorEventBusObserver);
    }

    public VB getViewDataBinding() {
        return mViewDataBinding;
    }

    public VM getViewModel() {
        return mViewModel;
    }

    public void performDependencyInjection() {
        AndroidInjection.inject(this);
    }

    protected void performDataBinding() {
        if (getLayoutId() != 0)
            mViewDataBinding = DataBindingUtil.setContentView(this, getLayoutId());

        try {
            mViewModel = new ViewModelProvider(this, factory).get(setViewModel());
        } catch (Exception ignored) {
        }

        if (mViewDataBinding != null) {
            mViewDataBinding.setVariable(getBindingVariable(), mViewModel);
            mViewDataBinding.executePendingBindings();
        }
    }

    private Context setLanguage(Context context) {
        String localLang = UserUtils.getInstance(context).getLanguage();
        Resources res = context.getResources();
        Locale newLocale = new Locale(localLang);
        Configuration configuration = new Configuration();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            configuration.setLocale(newLocale);
            LocaleList localeList = new LocaleList(newLocale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);

            context = context.createConfigurationContext(configuration);

        } else {
            configuration.locale = newLocale;
        }
        res.updateConfiguration(configuration, res.getDisplayMetrics());
        return context;
    }

    private void setLanguage() {
        String localLang = UserUtils.getInstance(this).getLanguage();

        Locale locale = new Locale(localLang);
        Locale.setDefault(locale);

        Configuration config = new Configuration();
        config.setLocale(locale);

        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    protected void confirmationDialog(String title, String message, final int action_type) {
        Dialog dialog = new Dialog(this);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        }

        DialogConfirmBinding dialogDataBinding = DialogConfirmBinding.inflate(LayoutInflater.from(this),
                null,
                false);

        dialog.setContentView(dialogDataBinding.getRoot());
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.textView, title);
        dialogDataBinding.textView.setText(title);
        BindingUtils.setFontDependingOnLanguage(dialogDataBinding.text, message);
        dialogDataBinding.text.setText(message);

        boolean canCancel = !(action_type == Constants.BIOMETRIC_AUTH_ACTION_TYPE);
        dialog.setCancelable(canCancel);
        dialog.setCanceledOnTouchOutside(canCancel);
        dialog.show();

        boolean isNotification = action_type == Constants.NOTIFICATIONS_ACTION_TYPE;
        dialogDataBinding.btnOk.setVisibility(isNotification ? View.VISIBLE : View.GONE);
        dialogDataBinding.buttonSubmit.setVisibility(isNotification ? View.GONE : View.VISIBLE);
        dialogDataBinding.cancelButton.setVisibility(isNotification ? View.GONE : View.VISIBLE);

        dialogDataBinding.buttonSubmit.setOnClickListener(v -> {
            dialog.dismiss();
            performAction(action_type);
        });

        dialogDataBinding.cancelButton.setOnClickListener(v -> {
            dialog.dismiss();
            performCancelAction(action_type);
        });

        dialogDataBinding.btnOk.setOnClickListener(v -> {
            dialog.dismiss();
            performAction(action_type);
        });
    }

    protected void performCancelAction(int action_type) {

    }

    protected void performAction(int action_type) {

    }

    protected void showProgress() {

        if (progressDialog == null) {
            progressDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
            progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
            progressDialog.setContentText(getString(R.string.loading));
            progressDialog.setCancelable(false);
        }
        progressDialog.show();
    }

    protected void hideProgress() {
        if (progressDialog != null)
            progressDialog.dismiss();
    }

    public void showToast(final String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void showToast(@StringRes final int stringRes) {
        showToast(getString(stringRes));
    }

    public void hideKeyboard() {
        View currentFocus = getCurrentFocus();
        if (currentFocus == null)
            currentFocus = new View(this);
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
    }

    public void showErrorDialog(Exception error) {
        if (error != null)
            DataRetrievalErrorDialog.show(error, view -> refresh(), getSupportFragmentManager());
    }

    public void refresh() {

    }

    public ActivityNavigation getActivityNavigator() {
        return activityNavigator;
    }

    protected void setToolbarTitle(Toolbar toolbar, String title) {
        toolbar.setTitleTextAppearance(this, R.style.ToolbarTitleText);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setTitle(title);
    }

    protected void onToolbarBackPressed() {
        Intent upIntent = NavUtils.getParentActivityIntent(this);
        if (upIntent != null && NavUtils.shouldUpRecreateTask(this, upIntent)) {
            TaskStackBuilder.create(this)
                    .addNextIntentWithParentStack(upIntent)
                    .startActivities();
        } else {
            onBackPressed();
        }
    }

    public void setCryptPasswordCallback(CryptPasswordCallback cryptPasswordCallback) {
        this.cryptPasswordCallback = cryptPasswordCallback;
    }

    @Nullable
    protected CiphertextWrapper getCipherTextWrapper() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return cryptographyManager.getCiphertextWrapperFromSharedPrefs(
                    getApplicationContext(),
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
            );
        }
        return null;
    }

    public boolean canUseBiometric() {
        return getCipherTextWrapper() != null &&
                canAuthBio();
    }

    protected boolean canAuthBio() {
        BiometricManager biometricManager = BiometricManager.from(getApplicationContext());
        int canAuthenticate = biometricManager.canAuthenticate(BIOMETRIC_STRONG);
        return canAuthenticate == BiometricManager.BIOMETRIC_SUCCESS;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void showBiometricPromptForEncryption() {
        String secretKeyName = getString(R.string.secret_key_name);
        Cipher cipher = cryptographyManager.getInitializedCipherForEncryption(secretKeyName);
        biometricPrompt = BiometricPromptUtils.INSTANCE.createBiometricPrompt(this, this::encryptAndStorePassword);
        promptInfo = BiometricPromptUtils.INSTANCE.createPromptInfo(this);
        biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected Unit encryptAndStorePassword(@NotNull BiometricPrompt.AuthenticationResult authResult, Boolean success) {
        if (success) {
            Cipher cipher = Objects.requireNonNull(authResult.getCryptoObject()).getCipher();
            assert cipher != null;
            CiphertextWrapper encryptedPasswordWrapper = cryptographyManager.encryptData(ConstantsKt.getUSER_PASS(), cipher);
            cryptographyManager.persistCiphertextWrapperToSharedPrefs(
                    encryptedPasswordWrapper,
                    getApplicationContext(),
                    SHARED_PREFS_FILENAME,
                    Context.MODE_PRIVATE,
                    CIPHERTEXT_WRAPPER
            );
        }
        if (cryptPasswordCallback != null)
            cryptPasswordCallback.encryptSuccess();
        return Unit.INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void showBiometricPromptForDecryption() {
        CiphertextWrapper ciphertextWrapper = getCipherTextWrapper();
        if (ciphertextWrapper != null) {
            String secretKeyName = getString(R.string.secret_key_name);
            Cipher cipher = cryptographyManager.getInitializedCipherForDecryption(secretKeyName, ciphertextWrapper.getInitializationVector());
            if (cipher == null) {
                UserUtils.getInstance(this).saveCIF4AskedUserBiometricUsage("");
                clearPasswordFromStorage();
                cryptPasswordCallback.keyInvalidated();
                return;
            }
            biometricPrompt = BiometricPromptUtils.INSTANCE.createBiometricPrompt(this, this::decryptPasswordFromStorage);
            promptInfo = BiometricPromptUtils.INSTANCE.createPromptInfo(this);
            biometricPrompt.authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected Unit decryptPasswordFromStorage(BiometricPrompt.AuthenticationResult authResult, Boolean success) {
        if (success) {
            CiphertextWrapper ciphertextWrapper = getCipherTextWrapper();
            if (ciphertextWrapper != null) {
                Cipher cipher = Objects.requireNonNull(authResult.getCryptoObject()).getCipher();
                assert cipher != null;
                String plaintext = cryptographyManager.decryptData(ciphertextWrapper.getCiphertext(), cipher);
                if (cryptPasswordCallback != null)
                    cryptPasswordCallback.decryptSuccess(plaintext);
            }
        }
        return Unit.INSTANCE;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void clearPasswordFromStorage() {
        cryptographyManager.clearCiphertextWrapperFromSharedPrefs(
                getApplicationContext(),
                SHARED_PREFS_FILENAME,
                Context.MODE_PRIVATE,
                CIPHERTEXT_WRAPPER
        );
    }
}
