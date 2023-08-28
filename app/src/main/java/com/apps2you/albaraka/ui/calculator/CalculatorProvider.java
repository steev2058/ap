package com.apps2you.albaraka.ui.calculator;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class CalculatorProvider {

    @ContributesAndroidInjector
    abstract FinancingFragment financingFragment();

    @ContributesAndroidInjector
    abstract ProfitsFragment profitsFragment();

    @ContributesAndroidInjector
    abstract DepositFragment depositFragment();
}
