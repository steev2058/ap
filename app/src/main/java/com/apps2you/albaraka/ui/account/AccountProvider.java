package com.apps2you.albaraka.ui.account;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class AccountProvider {

    @ContributesAndroidInjector(modules = {AccountModule.class})
    abstract AccountsFragment accountsFragment();
}
