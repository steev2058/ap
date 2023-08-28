package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class Partner(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("type") val type: String?,
    @SerializedName("account_number") val accountNumber: String?,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("is_active") val isActive: Int,
    @SerializedName("logo") val logo: String?,
    @SerializedName("code") val code: String, // for banks in SYGS
)