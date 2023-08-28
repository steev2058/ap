package com.apps2you.albaraka.ui.registration;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class LoginProvider {

    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract LoginFragment provideLoginFragment();

    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract ResetPasswordFragment provideVerficationCodeFragment();


    @ContributesAndroidInjector(modules = {LoginModule.class})
    abstract ResetPinFragment provideCreateAccountFragment();

}

