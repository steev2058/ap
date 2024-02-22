package com.apps2you.albaraka.ui.common.model;

public class HFProviderUI extends SelectableItem{
    private final int id;
    private final String code;


    public HFProviderUI(int id, String name, String imageURL,String code) {
        super(id, name, imageURL);
        this.id = id;
        this.code = code;
    }

    public int getProviderId() {
        return id;
    }

    public String getProviderCode() {return this.code;}


}
