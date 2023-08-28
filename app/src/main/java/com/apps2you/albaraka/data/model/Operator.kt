package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class Operator(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("image") val image: String,
    @SerializedName("prefixes") val prefixes: String,
    @SerializedName("postpaid_tax") val postPaidTax: BigDecimal = BigDecimal(0.0),
    @SerializedName("postpaid_fee") val postPaidFee: BigDecimal = BigDecimal(0.0),
    @SerializedName("max_limit") val maxLimit: BigDecimal = BigDecimal(0.0),
    @SerializedName("company") val company: String,
    @SerializedName("description") val description: String,
    @SerializedName("line_types") val lineTypes: ArrayList<LineType> = arrayListOf()
)