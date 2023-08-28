package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class TransferData(@SerializedName("id") val id: Int,
                   @SerializedName("fee") val fee: Int,
                   @SerializedName("name") val name: String,
)