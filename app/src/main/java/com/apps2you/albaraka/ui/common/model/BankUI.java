package com.apps2you.albaraka.ui.common.model;

public class BankUI extends SelectableItem{
    private final int id;
    private final String bankCode;


    public BankUI(int id, String name, String imageURL, String code) {
        super(id, name, imageURL);
        this.id = id;
        this.bankCode = code;
    }

    public int getBankId() {
        return id;
    }

    public String getBankCode() {
        return bankCode;
    }
}
