package com.apps2you.albaraka.ui;

import android.content.Intent;
import android.media.MediaPlayer;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.ActivitySplashBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.base.BaseViewModel;
import com.apps2you.albaraka.ui.home.HomeActivity;
import com.apps2you.albaraka.ui.registration.LoginActivity;

import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class SplashActivity extends BaseActivity<ActivitySplashBinding, BaseViewModel> {

    private MediaPlayer mediaPlayer;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public Class<BaseViewModel> setViewModel() {
        return BaseViewModel.class;
    }

    @Override
    public void setUpView() {

        if (UserUtils.getInstance(this).shouldRedirectToHome())//if user changed the language, we should restart the app withput need to re-login again
        {
            startActivity(new Intent(SplashActivity.this, HomeActivity.class));
            UserUtils.getInstance(this).setShouldRedirectToHome(false);
            finish();
            return;
        }
        if (Locale.getDefault().getLanguage().equals("ar"))//adding animated logo programmatically as the xml one isn't working correctly depending on localization
            mViewDataBinding.animationView.setAnimation(R.raw.logo_splash_ar);
        else
            mViewDataBinding.animationView.setAnimation(R.raw.logo_splash);

        UserUtils.getInstance(SplashActivity.this).clearUser();//clear user as we always log him out when he close the app even he didn't press log out
        if (UserUtils.getInstance(this).isSoundEnabled()) {
            mediaPlayer = MediaPlayer.create(this, R.raw.audio_splash);
//            mediaPlayer.setOnPreparedListener(mp -> {
            mediaPlayer.start();
//                mediaPlayer.setOnCompletionListener(m -> {

//                });
//            });
        }
//        else {
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                try {
                    if (mediaPlayer != null) {
                        mediaPlayer.reset();
                        mediaPlayer.release();
                    }
                } catch (IllegalStateException ignored) { }
                moveNext();
            }
        }, 3500);
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        try { // if user presses back before the splash finishes
            if (mediaPlayer != null) { // mediaPlayer can be null if sound effect is disabled
                mediaPlayer.reset();
                mediaPlayer.release();
            }
        } catch (IllegalStateException e) {
            // this crash happens when audio completes, and mediaPlayer is already reset and released
        }
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }

    private void moveNext() {
        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        finish();
    }
}
