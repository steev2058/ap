package com.apps2you.albaraka.ui.common.model;

import androidx.databinding.ObservableField;

public class SelectableItem {
    private final int id;
    private final String name;
    private final String imageURL;

    private final ObservableField<Boolean> isSelected = new ObservableField<>(false);

    public SelectableItem(int id, String name, String imageURL) {
        this.id = id;
        this.name = name;
        this.imageURL = imageURL;
    }

    public void setIsSelected(boolean isSelected) {
        this.isSelected.set(isSelected);
    }


    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getImageURL() {
        return imageURL;
    }

    public ObservableField<Boolean> getIsSelected() {
        return isSelected;
    }
}
