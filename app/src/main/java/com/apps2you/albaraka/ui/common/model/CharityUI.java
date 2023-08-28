package com.apps2you.albaraka.ui.common.model;

public class CharityUI extends SelectableItem{
    private final String accountNumber;


    public CharityUI(int id, String name, String imageURL, String accountNumber) {
        super(id, name, imageURL);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
