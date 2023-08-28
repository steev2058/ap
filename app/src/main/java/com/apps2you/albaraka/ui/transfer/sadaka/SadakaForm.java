package com.apps2you.albaraka.ui.transfer.sadaka;

import android.text.TextUtils;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class SadakaForm extends BaseTransferForm {
    public LocalizedStringLiveData phoneNumber = new LocalizedStringLiveData("");

    public SadakaForm(){
        addMutableLiveDataStringField(phoneNumber);
    }

    public boolean isPhoneEmpty(){
        return TextUtils.isEmpty(phoneNumber.getValue());
    }
}
