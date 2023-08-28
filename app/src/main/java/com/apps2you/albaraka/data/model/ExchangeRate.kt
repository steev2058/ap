package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.math.BigDecimal

data class ExchangeRate(@SerializedName("Buying_Rate") val buyingRate: BigDecimal,
                        @SerializedName("Selling_Rate") val sellingRate: BigDecimal,
                        @SerializedName("Middle_Rate") val middleRate: Double,
                        @SerializedName("currency") val currency: Currency?,
                        @SerializedName("DATE_RATE") val dateRate: String
)
