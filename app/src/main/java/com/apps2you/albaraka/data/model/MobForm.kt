package com.apps2you.albaraka.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName
import com.apps2you.albaraka.BR

data class MobForm(@SerializedName("id") val id: Int = -1) : BaseObservable() {

    @SerializedName("first_name")
    @get:Bindable
    var firstName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.firstName)
        }

    @SerializedName("last_name")
    @get:Bindable
    var lastName: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.lastName)
        }

    @SerializedName("client_status")
    @get:Bindable
    var clientStatus: String? = null

    @SerializedName("mobile_number")
    @get:Bindable
    var mobileNumber: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.mobileNumber)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("phone_number")
    @get:Bindable
    var phoneNumber: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.phoneNumber)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("email")
    @get:Bindable
    var email: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.email)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("contact_time")
    @get:Bindable
    var contactTime: String = "9-11"

    @SerializedName("complaint_title_id")
    @get:Bindable
    var complaintTitleID: Int? = null

    @SerializedName("branch_id")
    @get:Bindable
    var branchID: Int? = null

    @SerializedName("complaint_date")
    @get:Bindable
    var complaintDate: String? = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.complaintDate)
        }
        get() {
            return if (field!!.isEmpty()) null else field
        }

    @SerializedName("message")
    @get:Bindable
    var message: String = ""
        set(value) {
            field = value
            notifyPropertyChanged(BR.message)
        }
}
