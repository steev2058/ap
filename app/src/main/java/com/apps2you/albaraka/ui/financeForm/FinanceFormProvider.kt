package com.apps2you.albaraka.ui.financeForm

import com.apps2you.albaraka.ui.financeForm.fragments.FinanceFormFragment
import com.apps2you.albaraka.ui.financeForm.fragments.FinanceFormModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FinanceFormProvider {
    @ContributesAndroidInjector(modules = [FinanceFormModule::class])
    abstract fun FinanceFormFragment(): FinanceFormFragment
}