package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName


class DepositResult(@SerializedName("expectedDeposit") val expectedDeposit: String,
                    @SerializedName("currency") val currency: String,
                    @SerializedName("firstPatchDate") val firstPatchDate: String,
)