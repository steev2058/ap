package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class QrCode( @SerializedName("code") val code: String,
        @SerializedName("receiver_name") val receiverName: String)