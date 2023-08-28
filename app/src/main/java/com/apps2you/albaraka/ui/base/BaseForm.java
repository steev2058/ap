package com.apps2you.albaraka.ui.base;

import androidx.lifecycle.MutableLiveData;

import com.apps2you.albaraka.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaseForm {

    protected List<MutableLiveData<String>> mutableLiveDataFields = new ArrayList<>();

    @SafeVarargs
    protected final void addMutableLiveDataStringField(MutableLiveData<String>... mutableLiveDataFields) {
        this.mutableLiveDataFields.addAll(Arrays.asList(mutableLiveDataFields));
    }

    protected Integer stringValueToInt(MutableLiveData<String> liveData) {
        return liveData.getValue() == null ? null : Integer.parseInt(liveData.getValue());
    }

    public boolean noEmptyFields() {
        for (MutableLiveData<String> field : mutableLiveDataFields) {
            if (field.getValue() == null || field.getValue().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean allowed() {
        return noEmptyFields();
    }

    public int getErrorResource() {
        if (!noEmptyFields()) {
            return R.string.error_required;
        } else {
            return 0;
        }
    }
}
