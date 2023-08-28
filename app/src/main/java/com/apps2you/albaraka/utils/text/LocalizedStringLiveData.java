package com.apps2you.albaraka.utils.text;

import androidx.lifecycle.MutableLiveData;

public class LocalizedStringLiveData extends MutableLiveData<String> {
    public LocalizedStringLiveData(String value) {
        super(value);
    }

    public LocalizedStringLiveData() {
    }

    public String getLocalizedValue() {
        return TextUtils.toEnglishLocale(getValue());
    }

    public String getLocalizedNumber() {
        return TextUtils.toEnglishNumber(getValue());
    }
}
