package com.apps2you.albaraka.ui.atmCard;

import com.apps2you.albaraka.ui.common.dialogs.ConfirmPinDialog;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;


@Module
public abstract class AtmCardsProvider {

    @ContributesAndroidInjector
    abstract AtmCardsFragment atmCardsFragment();

    @ContributesAndroidInjector
    abstract AtmLimitsFragment atmLimitsFragment();

    @ContributesAndroidInjector
    abstract ConfirmPinDialog provideConfirmPinDialog();
}
