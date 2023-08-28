package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class FinancingType(
        @SerializedName("id") private val id: Int,
        @SerializedName("name") private val name: String,
        @SerializedName("maximum_financing_term") val maxTerm: Int
) : SpinnerItem {
    override fun getId(): Int = id

    override fun getName(): String = name
}