package com.apps2you.albaraka.data.remote.responseModel

import com.apps2you.albaraka.data.model.ExchangeRate
import com.google.gson.annotations.SerializedName

class ExchangeData(@SerializedName("exchange_rates") val exchangeRates: ArrayList<ExchangeRate>,
                   @SerializedName("max_date") val maxDate: String)