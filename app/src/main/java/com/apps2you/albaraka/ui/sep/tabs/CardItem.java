package com.apps2you.albaraka.ui.sep.tabs;

public class CardItem {

    private String text;
    private String iconUrl;



    public CardItem(String text, String iconUrl) {
        this.text = text;
        this.iconUrl = iconUrl;

    }

    public String getText() {
        return text;
    }

    public String getIconUrl() {
        return iconUrl;
    }
}
