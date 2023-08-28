package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

class Account(
        @SerializedName("os_gl_name") val type: String = "",
        @SerializedName("BRIEF_gl_name") val name: String = "",
        @SerializedName("os_add_reference") var number: String = "",
        @SerializedName("account_type_color") var account_type_color: String = "",
        @SerializedName("odec_cv_avail_bal") val balance: BigDecimal = BigDecimal(0.0),
        @SerializedName("currency") val currency: Currency = Currency(),
        @SerializedName("is_allow_to") val isAllowTo: Boolean = false, // transfer is allowed to this account
        @SerializedName("is_allow_from") val isAllowFrom: Boolean = false, // transfer is allowed from this account
        @SerializedName("ol_glcode") val accountCode: String = "" // this code defines if transfers are allowed from/to this account
) {

    override fun equals(other: Any?) = (other is Account)
            && number == other.number
}