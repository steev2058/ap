package com.apps2you.albaraka.data.model

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import  java.io.Serializable;

data class QuickService(@Expose @SerializedName("id") val id: Int,
                        @SerializedName("name") val name: String,
                        @Expose @SerializedName("priority") var priority: Int,
                        @Expose @Bindable @SerializedName("enabled") var enabled: Boolean,
                        @SerializedName("is_active") val isActive: Int) : Serializable, BaseObservable(){

    override fun toString(): String {
        return GsonBuilder() // only "id","priority", and "enabled" have to be included in the json object which will be sent to server
                // that's why they're annotated with @Expose
                .excludeFieldsWithoutExposeAnnotation()
                .create()
                .toJson(this, QuickService::class.java)
    }
}