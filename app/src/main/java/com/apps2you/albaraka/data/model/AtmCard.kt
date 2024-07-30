package com.apps2you.albaraka.data.model

import com.apps2you.albaraka.utils.Constants
import com.google.gson.annotations.SerializedName


class AtmCard(@SerializedName("caR_CHLD_NAME") val name: String = "",
              @SerializedName("caR_NUMB") val cardNumber: String = "",
              @SerializedName("caR_EXPI_DATE") val expiryDate: String = "",
              @SerializedName("ccO_TRAN_CUMU_MAX") val maxLimit: String,
              @SerializedName("gender") val gender: String,
              @SerializedName("is_active") val isActive: Boolean
) {
    fun isMale(): Boolean = gender == Constants.GENDER_MALE

    fun isBlack() : Boolean = cardNumber.startsWith("97602130") ||cardNumber.startsWith("97602134")
    fun isRed() : Boolean = cardNumber.startsWith("97602131")
    fun isSilver() : Boolean = cardNumber.startsWith("97602132")
    fun isGold() : Boolean = cardNumber.startsWith("97602133")


}