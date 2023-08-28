package com.apps2you.albaraka.ui.common.model;

public class SchoolUI extends SelectableItem{
    private final String accountNumber;
    public SchoolUI(int id, String name, String imageURL, String accountNumber) {
        super(id, name, imageURL);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
