package com.apps2you.albaraka.ui.common.model;

public class UniversityUI extends SelectableItem{
    private final String accountNumber;

    public UniversityUI(int id, String name, String imageURL, String accountNumber) {
        super(id, name, imageURL);
        this.accountNumber = accountNumber;
    }

    public String getAccountNumber() {
        return accountNumber;
    }
}
