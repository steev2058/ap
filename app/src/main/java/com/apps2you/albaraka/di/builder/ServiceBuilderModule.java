package com.apps2you.albaraka.di.builder;

import com.apps2you.albaraka.BarakaFirebaseMessagingService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuilderModule {

    @ContributesAndroidInjector()
    abstract BarakaFirebaseMessagingService barakaFirebaseMessagingService();
}
