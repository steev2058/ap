package com.apps2you.albaraka.ui.my_financing


import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingFragment
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingListFragment
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingModule
import com.apps2you.albaraka.ui.my_financing.fragments.MyFinancingTableFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class MyFinancingProvider {
    @ContributesAndroidInjector(modules = [MyFinancingModule::class])
    abstract fun MyFinancingFragment(): MyFinancingFragment


    @ContributesAndroidInjector(modules = [MyFinancingModule::class])
    abstract fun MyFinancingListFragment(): MyFinancingListFragment

    @ContributesAndroidInjector(modules = [MyFinancingModule::class])
    abstract fun MyFinancingTableFragment(): MyFinancingTableFragment
}