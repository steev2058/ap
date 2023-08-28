package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class SYGSTransferType(
    @SerializedName("id") val typeId: Int,
    @SerializedName("name") val typeName: String,
    @SerializedName("type_code") val typeCode: Int,
    @SerializedName("min_limit") val minLimit: Double,
    @SerializedName("max_limit") val maxLimit: Double): SpinnerItem {

    override fun getId(): Int = typeId

    override fun getName(): String = typeName

    }