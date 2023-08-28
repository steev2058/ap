package com.apps2you.albaraka.ui.transfer.payment.mobile;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.ui.base.BaseForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

import java.math.BigDecimal;

public class MobilePaymentForm extends BaseForm {
    public final MutableLiveData<MobilePaymentStatus> status = new MutableLiveData<>();

    public final LocalizedStringLiveData gsmNumber = new LocalizedStringLiveData();
    public final LocalizedStringLiveData amount = new LocalizedStringLiveData();

    public MobilePaymentForm() {
        addMutableLiveDataStringField(gsmNumber);
    }

    @Override
    public boolean allowed() {
        if (TextUtils.isEmpty(gsmNumber.getValue())){
            status.setValue(MobilePaymentStatus.GSM_ERROR);
            return false;
        }
        return true;
    }

    public boolean isAmountInvalid() {
        return new BigDecimal(amount.getLocalizedNumber()).compareTo(BigDecimal.ZERO) <= 0;
    }

    public enum MobilePaymentStatus{
        ALLOWED,
        GSM_ERROR
    }
}
