package com.apps2you.albaraka.ui.kyc

import com.apps2you.albaraka.ui.kyc.fragments.KycFragment
import com.apps2you.albaraka.ui.kyc.fragments.KycModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
@Module
abstract class KycProvider {
    @ContributesAndroidInjector(modules = [KycModule::class])
    abstract fun kycFragment(): KycFragment
}