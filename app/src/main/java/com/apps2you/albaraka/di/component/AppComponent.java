package com.apps2you.albaraka.di.component;


import android.app.Application;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.di.builder.ActivityBuilderModule;
import com.apps2you.albaraka.di.builder.FragmentBuilderModule;
import com.apps2you.albaraka.di.builder.ServiceBuilderModule;
import com.apps2you.albaraka.di.module.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AppModule.class,
        AndroidInjectionModule.class,
        ActivityBuilderModule.class,
        ServiceBuilderModule.class,
        FragmentBuilderModule.class
})
public interface AppComponent {

    void inject(MyApplication myApplication);

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}