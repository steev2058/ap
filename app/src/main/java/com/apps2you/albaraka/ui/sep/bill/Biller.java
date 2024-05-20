package com.apps2you.albaraka.ui.sep.bill;

import java.util.List;

public class Biller {
    private String billerCode;
    private String billerName;
    private List<Service> services;

    public Biller(String billerCode, String billerName, List<Service> services) {
        this.billerCode = billerCode;
        this.billerName = billerName;
        this.services = services;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public String getBillerName() {
        return billerName;
    }

    public List<Service> getServices() {
        return services;
    }
}
