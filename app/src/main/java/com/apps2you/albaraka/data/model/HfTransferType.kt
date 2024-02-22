package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class HfTransferType(
    @SerializedName("type_code") val typeCode: Int,
    @SerializedName("amount") val amount: Double,
    @SerializedName("min_limit") val minLimit: Double,
    @SerializedName("max_limit") val maxLimit: Double)

