package com.apps2you.albaraka.ui.sep;

import android.text.TextUtils;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class SEPForm extends BaseTransferForm {
    public LocalizedStringLiveData phoneNumber = new LocalizedStringLiveData("");

    public SEPForm(){
        addMutableLiveDataStringField(phoneNumber);
    }

    public boolean isPhoneEmpty(){
        return TextUtils.isEmpty(phoneNumber.getValue());
    }
}
