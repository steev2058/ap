package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class ProfitsResult(@SerializedName("expectedProfit") val expectedProfit: String,
                    @SerializedName("currency") val currency: String,
                    @SerializedName("quarter") val quarter: Int?
)