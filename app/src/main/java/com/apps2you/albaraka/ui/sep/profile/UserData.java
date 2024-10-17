package com.apps2you.albaraka.ui.sep.profile;

// UserData.java
public class UserData {
    private String arName;
    private String address;
    private String phone;
    private String cif;
    private String token;






    public void setToken(String token) {
        this.token = token;
    }

    public void setArName(String arName) {
        this.arName = arName;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getArName() {
        return arName;
    }
    public String getAddress() {
        return address;
    }


    public String getPhone() {
        return phone;
    }


    public void setCif(String cif) {
        this.cif = cif;
    }
    public String getCif() {
        return cif;
    }
    public String getToken() {
        return token;
    }
}
