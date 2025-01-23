package com.apps2you.albaraka.utils;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class NumberTextWatcher2 implements TextWatcher {
    private final DecimalFormat df;
    private final DecimalFormat dfNoDecimal;
    private boolean isEditing;

    public NumberTextWatcher2(EditText etAmount) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        symbols.setGroupingSeparator(',');
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("#,###.00", symbols);
        df.setGroupingUsed(true);

        dfNoDecimal = new DecimalFormat("#,##0", symbols);
        dfNoDecimal.setGroupingUsed(true);
        isEditing = false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        // No action needed here
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        // No action needed here
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (isEditing) return;
        isEditing = true;

        try {
            String originalString = s.toString();
            if (!originalString.isEmpty()) {
                String cleanString = originalString.replace(",", "");
                if (cleanString.contains(".")) {
                    s.replace(0, s.length(), df.format(Double.parseDouble(cleanString)));
                } else {
                    s.replace(0, s.length(), dfNoDecimal.format(Double.parseDouble(cleanString)));
                }
            }
        } catch (NumberFormatException e) {
            // Handle parse exception
        } finally {
            isEditing = false;
        }
    }
}
