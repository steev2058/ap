package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

class City(@SerializedName("id") var city_id: Int,
           @SerializedName("name") var city_name: String,
           @SerializedName("code") var code: String,
) : SpinnerItem {
    override fun getId(): Int {
        return city_id
    }

    override fun getName(): String {
        return city_name
    }

    fun getDefaultCity(name:String):City{
        return City(-1,name,"");
    }
}
