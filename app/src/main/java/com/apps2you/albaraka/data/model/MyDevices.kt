package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName


class MyDevices {
    @SerializedName("id")
    var id = 0

    @SerializedName("device")
    var device: String? = null

    @SerializedName("is_trusted")
    var isTrusted = 0

    @SerializedName("last_login")
    var lastLogin: String? = null
}

