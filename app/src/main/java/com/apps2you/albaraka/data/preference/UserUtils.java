package com.apps2you.albaraka.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.apps2you.albaraka.Keys;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.utils.SCEE;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.security.GeneralSecurityException;


public class UserUtils {

    private final String PREF_USER = "PREF_USER";
    private final String PREF_DEFAULT = "PREF_DEFAULT";
    private final Context context;


    public static UserUtils getInstance(Context context) {
        return new UserUtils(context);
    }

    public UserUtils(Context context) {
        this.context = context;
    }

    public void setLanguage(String lang) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putString("lang", lang).apply();
    }

    public String getLanguage() {
        // The default language of the app is Arabic
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getString("lang", "ar");
    }

    public void saveCIF4AskedUserBiometricUsage(String cif) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit()
                .putString("allow_bio_asked_cif", cif)
                .apply();
    }

    public String getCIF4AskedUserBiometricUsage() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getString("allow_bio_asked_cif", "");
    }

    public void saveCIFNumber(String cif) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putString("cif", cif).apply();
    }

    public String getCIFNumber() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getString("cif", null);
    }

    public void setPrivacyAgreed() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean("privacy_agreed", true).apply();
    }

    public boolean isPrivacyAgreed() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getBoolean("privacy_agreed", false);
    }

    public void setVisitorChecked(boolean isChecked) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean("visitor_checked", isChecked).apply();
    }

    public boolean isVisitorChecked() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getBoolean("visitor_checked", true);
    }

    public void setSoundEnabled(boolean enabled) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean("sound", enabled).apply();
    }

    public boolean isSoundEnabled() {
        // Sound effect in Splash should be enabled by default
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getBoolean("sound", true);
    }

    public void setShouldRedirectToHome(boolean enabled) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit().putBoolean("go_to_home", enabled).apply();
    }

    public boolean shouldRedirectToHome() {
        // Sound effect in Splash should be enabled by default
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getBoolean("go_to_home", false);
    }

    public void saveUser(User user) {
        Gson gson = new GsonBuilder().create();
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);

        user.setAccessToken(getEncryptedString(user.getAccessToken()));
        user.setRefreshToken(getEncryptedString(user.getRefreshToken()));

        mPrefs.edit().putString(PREF_USER, gson.toJson(user, User.class)).apply();
    }

    public User getUser() {
        Gson gson = new GsonBuilder().create();
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        User user = gson.fromJson(mPrefs.getString(PREF_USER, ""), User.class);
        if (user != null) {
            user.setAccessToken(getDecryptedString(user.getAccessToken()));
            user.setRefreshToken(getDecryptedString(user.getRefreshToken()));
        }
        return user;
    }

    private String getEncryptedString(String value){
        try {
            return SCEE.encryptString(value, Keys.INSTANCE.encryptionKey());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getDecryptedString(String value){
        try {
            return SCEE.decryptString(value, Keys.INSTANCE.encryptionKey());
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clearUser() {
        SharedPreferences mSharedPreference = context.getSharedPreferences(PREF_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = mSharedPreference.edit();
        editor.clear();
        editor.apply();
//        FirebaseMessaging.getInstance().unsubscribeFromTopic("");//unsubscribe from the lang topic in all cases (normal logout, auth error..)
    }

    public void saveFCMToken(String fcm_token) {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        mPrefs.edit()
                .putString("fcm_token", fcm_token)
                .apply();
    }

    public String getFCMToken() {
        SharedPreferences mPrefs = context.getSharedPreferences(PREF_DEFAULT, Context.MODE_PRIVATE);
        return mPrefs.getString("fcm_token", null);
    }
}
