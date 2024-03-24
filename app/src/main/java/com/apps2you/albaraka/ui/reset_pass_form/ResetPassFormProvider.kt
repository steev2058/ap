package com.apps2you.albaraka.ui.reset_pass_form


import com.apps2you.albaraka.ui.reset_pass_form.fragments.ResetPassFormFragment
import com.apps2you.albaraka.ui.reset_pass_form.fragments.ResetPassFormModule
import dagger.Module
import dagger.android.ContributesAndroidInjector



@Module
abstract class ResetPassFormProvider {

    @ContributesAndroidInjector(modules = [ResetPassFormModule::class])
    abstract fun resetPassFormFragment(): ResetPassFormFragment
}