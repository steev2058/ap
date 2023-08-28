package com.apps2you.albaraka.data.remote.responseModel

import com.google.gson.annotations.SerializedName

data class RefreshTokenData(
    @SerializedName("token_type") val tokenType: String,
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String
)