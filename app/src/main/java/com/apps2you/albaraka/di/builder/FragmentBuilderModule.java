package com.apps2you.albaraka.di.builder;

import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog;
import com.apps2you.albaraka.ui.sep.bill.BillDetailsFragment;
import com.apps2you.albaraka.ui.sep.bill.BillFragment;
import com.apps2you.albaraka.ui.sep.profile.UserSepProfileFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract BillFragment contributeBillFragment();
    @ContributesAndroidInjector
    abstract BillDetailsFragment contributeBillDetailsFragment();

    @ContributesAndroidInjector
    abstract UserSepProfileFragment conUserSepProfileFragment();


}
