package com.apps2you.albaraka.ui.more;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class MoreProvider {

    @ContributesAndroidInjector(modules = {MoreModule.class})
    abstract MoreFragment moreFragment();
}
