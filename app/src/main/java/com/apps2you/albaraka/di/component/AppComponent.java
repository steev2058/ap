package com.apps2you.albaraka.di.component;


import android.app.Application;

import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.di.builder.ActivityBuilderModule;
import com.apps2you.albaraka.di.builder.ServiceBuilderModule;
import com.apps2you.albaraka.di.module.AppModule;
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingListFragment;
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingTableFragment;
import com.apps2you.albaraka.ui.transfer.hf.HFFragment;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;

@Singleton
@Component(modules = {
        AppModule.class,
        AndroidInjectionModule.class,
        ActivityBuilderModule.class,
        ServiceBuilderModule.class
})
public interface AppComponent {

    void inject(MyApplication myApplication);
    void inject(HFFragment hfFragment);
    void inject(MyFinancingListFragment myFinancingListFragment);
    void inject(MyFinancingTableFragment myFinancingTableFragment);
    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(Application application);

        AppComponent build();
    }

}