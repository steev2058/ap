package com.apps2you.albaraka.ui.transfer.base;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.ui.base.BaseForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

import java.math.BigDecimal;

public class BaseTransferForm extends BaseForm {
    public final LocalizedStringLiveData amount = new LocalizedStringLiveData("");


    public final MutableLiveData<String> reason = new MutableLiveData<>("");

    public final MutableLiveData<String> bfirsname = new MutableLiveData<>("");

    public final MutableLiveData<String> bsecname = new MutableLiveData<>("");

    public final MutableLiveData<String> blastname = new MutableLiveData<>("");

    public BaseTransferForm() {
        addMutableLiveDataStringField(amount, reason);
    }

    public boolean isAmountEmpty() {
        return TextUtils.isEmpty(amount.getValue());
    }

    public boolean isAmountInvalid() {
        return new BigDecimal(amount.getLocalizedNumber()).compareTo(BigDecimal.ZERO) <= 0;
    }

    public boolean isReasonEmpty() {
        return TextUtils.isEmpty(reason.getValue());
    }



    public boolean isBFirstNameEmpty() {
        return TextUtils.isEmpty(bfirsname.getValue());
    }
    public boolean isBSecNameEmpty() {
        return TextUtils.isEmpty(bsecname.getValue());
    }
    public boolean isBLastNameEmpty() {
        return TextUtils.isEmpty(blastname.getValue());
    }
    public Integer getAmountValue(){
        return stringValueToInt(amount);
    }

}
