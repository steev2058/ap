package com.apps2you.albaraka.ui.sep.tabs;

import com.apps2you.albaraka.ui.sep.bill.Biller;

import java.util.List;

public class CardItem {

    private String text;
    private String iconUrl;

    private List<Biller> billerList;



    public CardItem(String text, String iconUrl, List<Biller> billerList) {
        this.text = text;
        this.iconUrl = iconUrl;
        this.billerList = billerList;

    }

    public String getText() {
        return text;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public List<Biller> getBillerList(){return this.billerList;}
}
