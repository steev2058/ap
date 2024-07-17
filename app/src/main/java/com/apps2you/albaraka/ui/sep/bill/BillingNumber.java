package com.apps2you.albaraka.ui.sep.bill;

import java.io.Serializable;

public class BillingNumber implements Serializable {
    private String arabicLabel;
    private String type;
    private String texts; // Added texts field

    public BillingNumber(String arabicLabel, String type, String texts) {
        this.arabicLabel = arabicLabel;
        this.type = type;
        this.texts = texts;
    }

    public String getArabicLabel() {
        return arabicLabel;
    }

    public String getType() {
        return type;
    }

    public String getTexts() {
        return texts;
    }

    public void setArabicLabel(String arabicLabel) {
        this.arabicLabel = arabicLabel;
    }
}
