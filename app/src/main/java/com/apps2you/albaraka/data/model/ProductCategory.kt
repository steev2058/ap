package com.apps2you.albaraka.data.model

import com.google.gson.annotations.SerializedName

data class ProductCategory(@SerializedName("id") val id: Int,
                           @SerializedName("name") val name: String,
                           @SerializedName("icon") val icon: String?,
                           @SerializedName("services") val services: ArrayList<ProductService>)
