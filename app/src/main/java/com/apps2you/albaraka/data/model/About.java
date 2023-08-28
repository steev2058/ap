package com.apps2you.albaraka.data.model;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class About extends BaseObservable {

    @SerializedName("content")
    @Expose
    @Bindable
    private String content = "";

    @SerializedName("facebook_link")
    private String facebook_link = "";
    @SerializedName("facebook_page_id")
    private String facebook_page_id = "";
    @SerializedName("linkedin_link")
    private String linkedin_link = "";
    @SerializedName("website_link")
    private String website_link = "";
    @SerializedName("telegram_link")
    private String telegram_link = "";
    @SerializedName("instagram_link")
    private String instagram_link = "";

    @SerializedName("tell_friend_button")
    private String shareText = "";

    @Bindable
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFacebook_link() {
        return facebook_link;
    }

    public String getLinkedin_link() {
        return linkedin_link;
    }

    public String getWebsite_link() {
        return website_link;
    }

    public String getShareText() {
        return shareText;
    }

    public String getFacebook_page_id() {
        return facebook_page_id;
    }

    public String getTelegram_link() {
        return telegram_link;
    }

    public String getInstagram_link() {
        return instagram_link;
    }
}
