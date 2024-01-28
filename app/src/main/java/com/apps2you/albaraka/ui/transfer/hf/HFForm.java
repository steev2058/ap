package com.apps2you.albaraka.ui.transfer.hf;

import android.text.TextUtils;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;


public class HFForm extends BaseTransferForm {
    public LocalizedStringLiveData phoneNumber = new LocalizedStringLiveData("");

    public HFForm(){
        addMutableLiveDataStringField(phoneNumber);
    }

    public boolean isPhoneEmpty(){
        return TextUtils.isEmpty(phoneNumber.getValue());
    }
}
