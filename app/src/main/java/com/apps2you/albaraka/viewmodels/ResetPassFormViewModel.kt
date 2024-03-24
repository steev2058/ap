package com.apps2you.albaraka.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import com.apps2you.albaraka.R
import com.apps2you.albaraka.data.model.Branch
import com.apps2you.albaraka.data.model.ResetPassForm
import com.apps2you.albaraka.data.model.SpinnerItem
import com.apps2you.albaraka.data.model.Title
import com.apps2you.albaraka.data.remote.networkUtils.Resource
import com.apps2you.albaraka.data.remote.repository.AppRepository
import com.apps2you.albaraka.ui.base.BaseViewModel
import com.apps2you.albaraka.utils.Constants
import javax.inject.Inject

class ResetPassFormViewModel @Inject constructor(private val appRepository: AppRepository) : BaseViewModel() {

    val resetPassForm = ResetPassForm()

    fun getClientSpinnerItems(context: Context): ArrayList<SpinnerItem> =
        arrayListOf(
            Title(context.getString(
                R.string
                    .current_or_potential_client)),
            Title(context.getString(
                R.string
                    .client)),
            Title(context.getString(
                R.string
                    .not_a_client))
        )

    fun getMessageTitleSpinnerItem(context: Context): ArrayList<Title> =
        arrayListOf(Title(context.getString(R.string.title_of_the_message)))

    fun getBranches(): LiveData<Resource<ArrayList<Branch>>> =
        appRepository.getBranches(Constants.TYPE_BRANCH)

    fun getMobFormTitles(): LiveData<Resource<ArrayList<Title>>> =
        appRepository.complaintTitles

    fun sendResetPassForm( nationa_id :String, cif:String, otp:String , reset_password:String, reset_pin:String): LiveData<Resource<String>> =
        appRepository.sendResetPassForm(nationa_id,cif,otp,reset_password,reset_pin)
}