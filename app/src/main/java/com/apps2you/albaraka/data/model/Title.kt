package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class Title(@SerializedName("name") val title_name: String,
                 @SerializedName("id") val title_id: Int = -1) : SpinnerItem {

    override fun getId(): Int {
        return title_id
    }

    override fun getName(): String {
        return title_name
    }
}
