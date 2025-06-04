package com.apps2you.albaraka;

import android.app.Application;
import android.app.Service;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatDelegate;

import com.apps2you.albaraka.data.preference.UserUtils;
//import com.apps2you.albaraka.di.component.DaggerAppComponent;
import com.apps2you.albaraka.di.component.DaggerAppComponent;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.common.busEvent.UnAuthorizedUserEvent;
import com.apps2you.albaraka.utils.ApplicationStateTracker;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.utils.SCEE;
import com.apps2you.albaraka.utils.bus.Bus;
import com.google.firebase.messaging.FirebaseMessaging;

import java.security.GeneralSecurityException;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasAndroidInjector;


public class MyApplication extends Application implements HasAndroidInjector {

    @Inject
    DispatchingAndroidInjector<Object> activityDispatchingInjector;
    @Inject
    DispatchingAndroidInjector<Service> dispatchingServiceInjector;


    public static boolean isLoggedIn = false;

    private final int INACTIVITY_THRESHOLD = 2 * 60 * 1000; // in milliseconds
    public static boolean isInBackground = false;
    public static boolean skipQuit = false;

    private static MyApplication sInstance;

    public static MyApplication getAppContext() {
        return sInstance;
    }

    private static synchronized void setInstance(MyApplication app) {
        sInstance = app;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        initializeLinks();
        initializeComponent();
        setInstance(this);
       // AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isDarkMode = prefs.getBoolean("dark_mode", false);

        AppCompatDelegate.setDefaultNightMode(
                isDarkMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO
        );

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String token = task.getResult();
                        UserUtils.getInstance(this).saveFCMToken(token);
                    }
                });

        registerApplicationStateCallback();
    }

    @Override
    public AndroidInjector<Object> androidInjector() {
        return activityDispatchingInjector;
    }

    public AndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

    private void initializeComponent() {
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    private void initializeLinks() {
        try {
            Constants.BASE_URL = SCEE.decryptString(Constants.BASE_URL, Keys.INSTANCE.encryptionKey());
            Constants.LINK_CREATE_ACCOUNT = SCEE.decryptString(Constants.LINK_CREATE_ACCOUNT, Keys.INSTANCE.encryptionKey());
            Constants.LINK_FINANCING = SCEE.decryptString(Constants.LINK_FINANCING, Keys.INSTANCE.encryptionKey());
            Constants.LINK_ORDER_ATM = SCEE.decryptString(Constants.LINK_ORDER_ATM, Keys.INSTANCE.encryptionKey());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }


    private void registerApplicationStateCallback() {
        registerActivityLifecycleCallbacks(new ApplicationStateTracker(
                new ApplicationStateTracker.ApplicationStateCallback() {
                    @Override
                    public void applicationWentToForeground() {
                        long currentUserInteraction = System.currentTimeMillis();
                        if (isInBackground && isLoggedIn && !skipQuit ){
                            Bus.instance().publish(UnAuthorizedUserEvent.getInstance());
                        }
                        isInBackground = false;
                        skipQuit = false;
                    }

                    @Override
                    public void applicationWentToBackground() {
                        isInBackground = true;

                    }
                }
        ));
    }
}