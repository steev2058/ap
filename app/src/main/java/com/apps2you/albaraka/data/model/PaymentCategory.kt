package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class PaymentCategory(
    @SerializedName("id") val categoryId: Int,
    @SerializedName("name") val categoryName: String,
    @SerializedName("amount") val amount: BigDecimal = BigDecimal(0.0),
    @SerializedName("line_type_id") val lineTypeId: Int,
    @SerializedName("tax") val tax: BigDecimal = BigDecimal(0.0),
    @SerializedName("fees") val fees: BigDecimal = BigDecimal(0.0),
    @SerializedName("segment_id") val segmentId: Int
) : SpinnerItem {

    override fun getId(): Int = categoryId

    override fun getName(): String = categoryName

    fun getTotalCost(): BigDecimal = amount.add(tax).add(fees)
}