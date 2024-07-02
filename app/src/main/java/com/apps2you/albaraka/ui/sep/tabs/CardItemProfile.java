package com.apps2you.albaraka.ui.sep.tabs;

public class CardItemProfile {
    private String title;

    private String id;
    private String description;
    private String serviceNameAr;
    private String billingNo;
    private String iconUrl;
    private int isDeleted;

    public CardItemProfile(String title, String description,String serviceNameAr, String billingNo, String iconUrl, int isDeleted,String id)  {
        this.title = title;
        this.description = description;
        this.serviceNameAr = serviceNameAr;
        this.billingNo = billingNo;
        this.iconUrl = iconUrl;
        this.isDeleted = isDeleted;
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceNameAr() {
        return serviceNameAr;
    }

    public String getId() {
        return id;
    }

    public String getBillingNo() {
        return billingNo;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public int getIsDeleted() {
        return isDeleted;
    }
}
