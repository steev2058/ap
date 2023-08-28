package com.apps2you.albaraka.data.remote.responseModel

import com.google.gson.annotations.SerializedName

class CalculatorData<T : Any>(@SerializedName("details") val details: T,
                              @SerializedName("calculatorMessage") val calculatorMessage: String
)