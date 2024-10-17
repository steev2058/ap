package com.apps2you.albaraka.ui.sep.tabs;

import com.apps2you.albaraka.ui.sep.bill.Biller;

import java.io.Serializable;
import java.util.List;

public class Category implements Serializable {
    private String categId;
    private String categName;
    private List<Biller> billers;

    public Category(String categId, String categName, List<Biller> billers) {
        this.categId = categId;
        this.categName = categName;
        this.billers = billers;
    }

    public String getCategId() {
        return categId;
    }

    public String getCategName() {
        return categName;
    }

    public List<Biller> getBillers() {
        return billers;
    }
}
