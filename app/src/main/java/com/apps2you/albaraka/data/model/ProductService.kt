package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ProductService(@SerializedName("id") val id: Int,
                          @SerializedName("name") val name: String,
                          @SerializedName("description") val description: String,
                          @SerializedName("image") val image: String?) : Serializable
