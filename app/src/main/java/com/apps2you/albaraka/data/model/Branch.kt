package com.apps2you.albaraka.data.model

import com.apps2you.albaraka.utils.Constants
import com.google.gson.annotations.SerializedName

class Branch(@SerializedName("id") val branch_id: Int,
             @SerializedName("name") val branch_name: String,
             @SerializedName("address") val address: String,
             @SerializedName("phone_number") val phoneNumber: String,
             @SerializedName("image") val imageUrl: String,
             @SerializedName("longitude") val longitude: Double,
             @SerializedName("latitude") val latitude: Double,
             @SerializedName("type") val type: String ,// Constants.TYPE_BRANCH = "branch", Constants.TYPE_ATM = "atm", TYPE_POS = "POS"
             @SerializedName("is_avaliable") val is_avaliable: Int
) : SpinnerItem {
    override fun getId(): Int {
        return branch_id
    }

    override fun getName(): String {
        return branch_name
    }

    fun isBranch(): Boolean {
        return type == Constants.TYPE_BRANCH
    }


    fun isAvaliable(): Boolean {
        return is_avaliable == 1;
    }

    fun isAtm(): Boolean {
        return type == Constants.TYPE_ATM
    }

    fun isPos(): Boolean {
        return type == Constants.TYPE_POS
    }

    fun isMerchant(): Boolean {
        return type == Constants.TYPE_MERCHANT
    }
}

fun getDefault(name: String): Branch {
    return Branch(-1,
        name,
        "",
        "",
        "",
        0.0,
        0.0,
        "",
        0
    )
}