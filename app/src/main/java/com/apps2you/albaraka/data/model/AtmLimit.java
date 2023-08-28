package com.apps2you.albaraka.data.model;

import androidx.databinding.ObservableField;

import com.google.gson.annotations.SerializedName;


public class AtmLimit {

    @SerializedName("limit")
    private String limit;

    @SerializedName("fee")
    private String fee;

    private final ObservableField<Boolean> isSelected= new ObservableField<>(false);



    public String getLimit() {
        return limit;
    }

    public String getFee() {
        return fee;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }

    public ObservableField<Boolean> getIsSelected() {
        return isSelected;
    }
}
