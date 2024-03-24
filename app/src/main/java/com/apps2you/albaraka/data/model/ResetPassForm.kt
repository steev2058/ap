package com.apps2you.albaraka.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.apps2you.albaraka.BR
import com.google.gson.annotations.SerializedName


data class ResetPassForm(@SerializedName("id") val id: Int = -1) : BaseObservable() {


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


    @SerializedName("reset_password")
    @get:Bindable
    var reset_password: String? = "false"
        set(value) {
            field = value
            notifyPropertyChanged(BR.reset_password)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("reset_pin")
    @get:Bindable
    var reset_pin: String? = "false"
        set(value) {
            field = value
            notifyPropertyChanged(BR.reset_pin)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("complaint_title_id")
    @get:Bindable
    var complaintTitleID: Int? = null






}
