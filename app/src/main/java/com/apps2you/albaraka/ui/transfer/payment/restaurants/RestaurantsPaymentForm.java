package com.apps2you.albaraka.ui.transfer.payment.restaurants;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class RestaurantsPaymentForm extends BaseTransferForm {
    public final MutableLiveData<FormStatus> formStatus = new MutableLiveData<>();

    public final LocalizedStringLiveData billNumber = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData tips = new LocalizedStringLiveData("");

    public RestaurantsPaymentForm() {
        addMutableLiveDataStringField(billNumber, tips);
    }

    @Override
    public boolean allowed() {
        if (TextUtils.isEmpty(amount.getValue())) {
            formStatus.setValue(FormStatus.ERROR_AMOUNT);
            return false;
        }
        if (TextUtils.isEmpty(reason.getValue())) {
            formStatus.setValue(FormStatus.ERROR_REASON);
            return false;
        }
        return true;
    }

    public enum FormStatus {
        ALLOWED,
        ERROR_AMOUNT,
        ERROR_REASON
    }
}
