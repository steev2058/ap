package com.apps2you.albaraka.data.remote.responseModel

import com.apps2you.albaraka.data.model.Partner
import com.apps2you.albaraka.data.model.SYGSTransferType
import com.google.gson.annotations.SerializedName


class SYGSData(
        @SerializedName("banks") val banks:List<Partner>,
        @SerializedName("terms") val terms: String,
        @SerializedName("types") val types:ArrayList<SYGSTransferType> = arrayListOf())