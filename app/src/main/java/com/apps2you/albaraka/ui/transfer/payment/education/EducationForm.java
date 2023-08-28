package com.apps2you.albaraka.ui.transfer.payment.education;

import android.text.TextUtils;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.utils.text.LocalizedStringLiveData;

public class EducationForm extends BaseTransferForm {
    public final MutableLiveData<FormStatus> formStatus = new MutableLiveData<>();

    public final MutableLiveData<String> studentName = new MutableLiveData<>("");
    public final LocalizedStringLiveData studentNumber = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData studentYear = new LocalizedStringLiveData("");
    public final LocalizedStringLiveData phone = new LocalizedStringLiveData("");

    public EducationForm() {
        addMutableLiveDataStringField(studentName, studentNumber, studentYear, phone);
    }

    @Override
    public boolean allowed() {
        if (TextUtils.isEmpty(studentName.getValue())) {
            formStatus.setValue(FormStatus.ERROR_NAME);
        } else if (TextUtils.isEmpty(studentNumber.getValue())) {
            formStatus.setValue(FormStatus.ERROR_NUMBER);
        } else if (TextUtils.isEmpty(studentYear.getValue())) {
            formStatus.setValue(FormStatus.ERROR_YEAR);
        } else if (TextUtils.isEmpty(phone.getValue())) {
            formStatus.setValue(FormStatus.ERROR_PHONE);
        } else if (TextUtils.isEmpty(amount.getValue())) {
            formStatus.setValue(FormStatus.ERROR_AMOUNT);
        } else if (TextUtils.isEmpty(reason.getValue())) {
            formStatus.setValue(FormStatus.ERROR_REASON);
        } else {
            return true;
        }
        return false;
    }

    public enum FormStatus {
        ALLOWED,
        ERROR_NAME,
        ERROR_NUMBER,
        ERROR_YEAR,
        ERROR_PHONE,
        ERROR_AMOUNT,
        ERROR_REASON
    }
}
