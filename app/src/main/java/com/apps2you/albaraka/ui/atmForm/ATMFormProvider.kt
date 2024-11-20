package com.apps2you.albaraka.ui.atmForm

import com.apps2you.albaraka.ui.atmForm.fragments.ATMFormFragment
import com.apps2you.albaraka.ui.atmForm.fragments.ATMFormModule
import dagger.Module
import dagger.android.ContributesAndroidInjector


@Module
abstract class ATMFormProvider {

    @ContributesAndroidInjector(modules = [ATMFormModule::class])
    abstract fun atmFormFragment(): ATMFormFragment
}