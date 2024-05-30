package com.apps2you.albaraka.di.builder;

import com.apps2you.albaraka.ui.sep.bill.BillFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class FragmentBuilderModule {

    @ContributesAndroidInjector
    abstract BillFragment contributeBillFragment();
}
