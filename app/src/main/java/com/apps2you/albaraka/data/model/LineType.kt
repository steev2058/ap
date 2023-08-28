package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class LineType(
    @SerializedName("id") val lineId: Int,
    @SerializedName("name") val lineName: String,
    @SerializedName("tax") val tax: BigDecimal = BigDecimal(0.0),
    @SerializedName("charges") val charges: BigDecimal = BigDecimal(0.0),
    @SerializedName("provider_id") val providerId: Int,
    @SerializedName("type") val type: String,
    @SerializedName("payment_categories") val paymentCategories: ArrayList<PaymentCategory> = arrayListOf()
) : SpinnerItem {
    
    override fun getId(): Int = lineId

    override fun getName(): String = lineName
}