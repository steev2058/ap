package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class NotificationContent(
    @SerializedName("id") val id: Int = -1,
    @SerializedName("type") val type: Int = -1,
    @SerializedName("title") val title: String,
    @SerializedName("body") val text: String,
    @SerializedName("od_trans_no") val originalTransactionId: Int, // the transaction id at alBaraka backend
    @SerializedName("created_at") val createdAt: String = "",
    @SerializedName("branch_code") val branchCode: String?
) : Serializable
