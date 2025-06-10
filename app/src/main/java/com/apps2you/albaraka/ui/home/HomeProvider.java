package com.apps2you.albaraka.ui.home;

import com.apps2you.albaraka.ui.account.AccountModule;
import com.apps2you.albaraka.ui.account.AccountsFragment;
import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog;
import com.apps2you.albaraka.ui.more.MoreFragment;
import com.apps2you.albaraka.ui.more.MoreModule;
import com.apps2you.albaraka.ui.payment.PaymentFragment;
import com.apps2you.albaraka.ui.payment.PaymentModule;
import com.apps2you.albaraka.ui.transfer.di.TransferModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class HomeProvider {

    @ContributesAndroidInjector(modules = {HomeModule.class})
    abstract GuestHomeFragment guestHomeFragment();

    @ContributesAndroidInjector(modules = {HomeModule.class})
    abstract HomeFragment homeFragment();
    @ContributesAndroidInjector(modules = {HomeModule.class})
    abstract ConfirmPinDialog provideConfirmPinDialog();
    @ContributesAndroidInjector(modules = {AccountModule.class})
    abstract AccountsFragment accountsFragment();

    @ContributesAndroidInjector(modules = {PaymentModule.class})
    abstract PaymentFragment paymentFragment();

    @ContributesAndroidInjector(modules = {MoreModule.class})
    abstract MoreFragment moreFragment();
}
