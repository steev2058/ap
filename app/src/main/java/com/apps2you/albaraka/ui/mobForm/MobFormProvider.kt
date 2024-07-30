package com.apps2you.albaraka.ui.mobForm
import com.apps2you.albaraka.ui.mobForm.fragments.MobFormFragment
import com.apps2you.albaraka.ui.mobForm.fragments.MobFormModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class MobFormProvider {

    @ContributesAndroidInjector(modules = [MobFormModule::class])
    abstract fun mobFormFragment(): MobFormFragment
}