package com.apps2you.albaraka.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.apps2you.albaraka.BR;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class User extends BaseObservable {

    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("CIF")
    @Expose
    @Bindable
    private String cif_number;

    @SerializedName("device")
    @Expose
    @Bindable
    private String device;

    @SerializedName("password")
    @Expose
    @Bindable
    private String password;

    @SerializedName("pin_code")
    @Expose
    @Bindable
    private String pin_code = "";

    @SerializedName("access_token")
    @Expose
    private String accessToken;

    @SerializedName("refresh_token")
    @Expose
    private String refreshToken;

    @SerializedName("bills_payment_token")
    @Expose
    private String billsPaymentToken;

    @SerializedName("last_login")
    private String lastLogin;

    @SerializedName("is_first_time")
    private int isFirstLogin;

    @SerializedName("is_first_password")
    private int shouldChangePassword;

    @SerializedName("english_name")
    private String englishName;

    @SerializedName("arabic_name")
    private String arabicName;

    @SerializedName("lang")
    private String clientLang;

    @SerializedName("notification_counter")
    private int notificationCounter;

    @SerializedName("enable_notifications")
    private int enableNotifications;

    @SerializedName("phone_number")
    private String phone;

    @SerializedName("check_visitor")
    private boolean hideServices;

    @Bindable
    public String getCif_number() {
        return cif_number;
    }

    @Bindable
    public String getDevice() {
        return device;
    }
    @Bindable
    public String getBillsPaymentToken() {
        return billsPaymentToken;
    }
    public void setBillsPaymentToken(String billsPaymentToken) {
        this.billsPaymentToken = billsPaymentToken;
    }

    public void setCif_number(String cif_number) {
        this.cif_number = cif_number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
        notifyPropertyChanged(BR.password);
    }

    public String getClientLang() {
        return clientLang;
    }

    public void setClientLang(String clientLang) {
        this.clientLang = clientLang;
    }

    public String getPin_code() {
        return pin_code;
    }

    public String getLastLogin() {
        return lastLogin;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getArabicName() {
        return arabicName;
    }

    public String getUserName(String lang) {
        if (lang.equals("ar"))
            return getArabicName();
        return getEnglishName();
    }

    public int getNotificationCounter() {
        return notificationCounter;
    }

    public void setNotificationCounter(int notificationCounter) {
        this.notificationCounter = notificationCounter;
    }

    public int getEnableNotifications() {
        return enableNotifications;
    }

    public void setEnableNotifications(int enableNotifications) {
        this.enableNotifications = enableNotifications;
    }

    public boolean shouldChangePassword() {
        return shouldChangePassword == 1;
    }

    public boolean shouldChangePinCode() {
        return isFirstLogin == 1;
    }


    public String getPhone() {
        return phone;
    }

    public boolean isHideServices() {
        return hideServices;
    }

    public void setHideServices(boolean hideServices) {
        this.hideServices = hideServices;
    }
}
