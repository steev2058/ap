package com.apps2you.albaraka.ui.sep.bill;


public   class BillingNumber {
    private String arabicLabel;
    private String type;

    public BillingNumber(String arabicLabel, String type) {
        this.arabicLabel = arabicLabel;
        this.type = type;
    }

    public String getArabicLabel() {
        return arabicLabel;
    }

    public String getType() {
        return type;
    }
}