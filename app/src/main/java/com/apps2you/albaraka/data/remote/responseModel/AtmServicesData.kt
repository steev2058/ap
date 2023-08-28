package com.apps2you.albaraka.data.remote.responseModel

import com.apps2you.albaraka.data.model.AtmLimit
import com.google.gson.annotations.SerializedName


class AtmServicesData(
        @SerializedName("change_pin_fee") val resetPinFee: String,
        @SerializedName("limits") val limits: ArrayList<AtmLimit>,
)