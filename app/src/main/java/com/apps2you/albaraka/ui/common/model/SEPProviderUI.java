package com.apps2you.albaraka.ui.common.model;

public class SEPProviderUI extends SelectableItem{
    private final int id;


    public SEPProviderUI(int id, String name, String imageURL) {
        super(id, name, imageURL);
        this.id = id;
    }

    public int getProviderId() {
        return id;
    }
}
