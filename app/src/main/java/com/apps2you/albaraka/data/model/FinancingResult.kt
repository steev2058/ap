package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class FinancingResult(@SerializedName("minFirstPatch") val minFirstPayment: String,
                      @SerializedName("fundingValue") val fundingValue: String,
                      @SerializedName("profitPercentage") val profitPercentage: String,
                      @SerializedName("maximumFinancingTerm") val maxFinancingTerm: String,
                      @SerializedName("monthlyInstallment") val monthlyInstallment: String,
                      @SerializedName("minimumRequiredMonthlyIncome") val minMonthlyIncome: String
)