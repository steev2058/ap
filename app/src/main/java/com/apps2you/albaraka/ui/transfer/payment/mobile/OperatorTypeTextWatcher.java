package com.apps2you.albaraka.ui.transfer.payment.mobile;

import android.text.Editable;
import android.text.TextWatcher;

import com.apps2you.albaraka.data.model.Operator;
import com.apps2you.albaraka.utils.text.TextUtils;

import java.util.Arrays;
import java.util.List;

public class OperatorTypeTextWatcher implements TextWatcher {
    private final OperatorChangeListener operatorChangeListener;
    private List<Operator> operators;


    public OperatorTypeTextWatcher(OperatorChangeListener operatorChangeListener) {
        this.operatorChangeListener = operatorChangeListener;
    }


    public void setOperators(List<Operator> operators) {
        this.operators = operators;
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        if (operators != null &&
                TextUtils.isValidPhoneNumber(
                        TextUtils.withoutCountryCode(charSequence.toString())
                )
        ) {
            try {
                Operator matchedOperator = operators.stream()
                        .filter(
                                operator ->
                                        Arrays.stream(operator.getPrefixes().split(","))
                                                .anyMatch(prefix ->
                                                        TextUtils.withoutCountryCode(charSequence.toString()).startsWith(prefix)
                                                )
                        ).findFirst().orElse(null);

                operatorChangeListener.onOperatorMatched(matchedOperator);
            } catch (Exception e) {
                e.printStackTrace();
                operatorChangeListener.onOperatorMatched(null);
            }
        }else {
            operatorChangeListener.onOperatorMatched(null);
        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    public interface OperatorChangeListener{
        void onOperatorMatched(Operator operator);
    }
}
