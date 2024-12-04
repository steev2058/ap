package com.apps2you.albaraka.ui.sep.tabs;

public class CardItemProfile {
    private String title;

    private String id;
    private String description;
    private String serviceNameAr;



    private String serviceNameEn;
    private String billingNo;
    private String iconUrl;
    private int isDeleted;

    private int auto_pay;

    private String billLabelAr;
    private String billLabel;

    private String billerCode;

    private String default_account;
    private int max_amount;


    public void setTitle(String title) {
        this.title = title;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setServiceNameAr(String serviceNameAr) {
        this.serviceNameAr = serviceNameAr;
    }

    public void setServiceNameEn(String serviceNameEn) {
        this.serviceNameEn = serviceNameEn;
    }

    public void setBillingNo(String billingNo) {
        this.billingNo = billingNo;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setAuto_pay(int auto_pay) {
        this.auto_pay = auto_pay;
    }

    public void setBillLabelAr(String billLabelAr) {
        this.billLabelAr = billLabelAr;
    }

    public void setBillLabel(String billLabel) {
        this.billLabel = billLabel;
    }

    public void setDefault_account(String default_account) {
        this.default_account = default_account;
    }

    public void setMax_amount(int max_amount) {
        this.max_amount = max_amount;
    }

    public CardItemProfile(){}

    public CardItemProfile(String title, String description, String serviceNameAr, String billingNo, String iconUrl, int isDeleted, String id, String billerCode, int auto_pay, String default_account, int max_amount)  {
        this.title = title;
        this.description = description;
        this.serviceNameAr = serviceNameAr;
        this.billingNo = billingNo;
        this.iconUrl = iconUrl;
        this.isDeleted = isDeleted;
        this.id = id;
        this.billerCode = billerCode;
        this.auto_pay = auto_pay;
        this.default_account = default_account;
        this.max_amount = max_amount;
    }
    public String getServiceNameEn() {
        return serviceNameEn;
    }

    public String getBillLabelAr() {
        return billLabelAr;
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
    public String getBillLabel() {
        return billLabel;
    }

    public String getBillerCode() {
        return billerCode;
    }

    public void setBillerCode(String billerCode) {
        this.billerCode = billerCode;
    }

    public int getAuto_pay() {
        return auto_pay;
    }

    public String getDefault_account() {
        return default_account;
    }

    public int getMax_amount() {
        return max_amount;
    }
}
