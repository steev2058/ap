package com.apps2you.albaraka.data.model

import android.text.TextUtils
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class FavoriteAccount(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("GSM") val GSM: String?,
    @SerializedName("CIF") val CIF: String?,
    @SerializedName("client_id") val clientId: Int
): Serializable{
    fun getNumber() : String? = if (TextUtils.isEmpty(CIF)) GSM else CIF
    fun getTransferChannelType() : TransferChannelType = if (TextUtils.isEmpty(CIF)) TransferChannelType.GSM else TransferChannelType.CIF

    override fun equals(other: Any?)
            = (other is FavoriteAccount)
            && id == other.id

    override fun hashCode(): Int {
        return id
    }
}