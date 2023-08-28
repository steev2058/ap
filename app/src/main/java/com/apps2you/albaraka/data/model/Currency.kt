package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class Currency(
        @SerializedName("id") private val id: String = "",
        @SerializedName("symbol") val symbol: String = "",
        @SerializedName("name") private val name: String = "",
        @SerializedName("currency") val currency: String = "",
        @SerializedName("flag") val flag: String = "",
        @SerializedName("code") val code: String = "",
        @SerializedName("updated_at")
        val updated_at: String = ""
) : SpinnerItem {
    override fun getId(): Int = id.toInt()

    override fun getName(): String = if (name.isNotEmpty()) name else currency
}