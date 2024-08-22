package com.apps2you.albaraka.ui.my_financing


import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingFragment
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class MyFinancingProvider {
    @ContributesAndroidInjector(modules = [MyFinancingModule::class])
    abstract fun MyFinancingFragment(): MyFinancingFragment
}