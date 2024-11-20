package com.apps2you.albaraka.ui.my_financing2


import com.apps2you.albaraka.ui.my_financing2.fragments.MyFinancingFragment2
import com.apps2you.albaraka.ui.my_financing2.fragments.MyFinancingModule2
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class MyFinancing2Provider {
    @ContributesAndroidInjector(modules = [MyFinancingModule2::class])
    abstract fun MyFinancingFragment2(): MyFinancingFragment2
}