package com.apps2you.albaraka.ui.common.model;

public class ADSLProviderUI extends SelectableItem{
    private final int id;


    public ADSLProviderUI(int id, String name, String imageURL) {
        super(id, name, imageURL);
        this.id = id;
    }

    public int getProviderId() {
        return id;
    }
}
