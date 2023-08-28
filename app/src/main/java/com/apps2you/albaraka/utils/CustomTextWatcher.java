package com.apps2you.albaraka.utils;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputLayout;


public class CustomTextWatcher implements TextWatcher {

    private TextInputLayout mInputLayout;

    public CustomTextWatcher(TextInputLayout inputLayout) {
        mInputLayout = inputLayout;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        mInputLayout.setErrorEnabled(false);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
