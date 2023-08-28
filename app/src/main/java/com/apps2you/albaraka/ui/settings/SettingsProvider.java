package com.apps2you.albaraka.ui.settings;


import com.apps2you.albaraka.ui.registration.ResetPasswordFragment;
import com.apps2you.albaraka.ui.registration.ResetPinFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class SettingsProvider {

    @ContributesAndroidInjector(modules = {SettingsModule.class})
    abstract ResetPinFragment resetPinFragment();

    @ContributesAndroidInjector(modules = {SettingsModule.class})
    abstract ResetPasswordFragment resetPasswordFragment();


    @ContributesAndroidInjector(modules = {SettingsModule.class})
    abstract otpCodeFragment otpCodeFragment();
}
