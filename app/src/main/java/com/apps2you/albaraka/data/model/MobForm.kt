package com.apps2you.albaraka.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.apps2you.albaraka.BR

data class MobForm(@SerializedName("id") val id: Int = -1) : BaseObservable() {

    @SerializedName("national_id")
    @get:Bindable
    var national_id: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.national_id)
        }

    @SerializedName("cif_id")
    @get:Bindable
    var cif_id: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.cif_number)
        }



    @SerializedName("mobile_id")
    @get:Bindable
    var mobileNumber: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.mobileNumber)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("captcha_challenge")
    @get:Bindable
    var captcha_challenge: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.captcha_challenge)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }




    @SerializedName("complaint_title_id")
    @get:Bindable
    var complaintTitleID: Int? = null






}
