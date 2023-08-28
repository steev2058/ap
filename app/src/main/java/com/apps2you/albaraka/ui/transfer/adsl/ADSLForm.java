package com.apps2you.albaraka.ui.transfer.adsl;

import android.text.TextUtils;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class ADSLForm extends BaseTransferForm {
    public LocalizedStringLiveData phoneNumber = new LocalizedStringLiveData("");

    public ADSLForm(){
        addMutableLiveDataStringField(phoneNumber);
    }

    public boolean isPhoneEmpty(){
        return TextUtils.isEmpty(phoneNumber.getValue());
    }
}
