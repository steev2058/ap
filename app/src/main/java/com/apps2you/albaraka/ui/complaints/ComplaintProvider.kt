package com.apps2you.albaraka.ui.complaints

import com.apps2you.albaraka.ui.complaints.fragments.ComplaintFragment
import com.apps2you.albaraka.ui.complaints.fragments.ComplaintModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ComplaintProvider {

    @ContributesAndroidInjector(modules = [ComplaintModule::class])
    abstract fun complaintFragment(): ComplaintFragment
}