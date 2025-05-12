package com.apps2you.albaraka.utils;

import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.Locale;

public class NumberTextWatcher implements TextWatcher {

    private final DecimalFormat df;
    private final DecimalFormat dfnd;
    private boolean hasFractionalPart;

    private final EditText et;

    public NumberTextWatcher(EditText et) {
        df = new DecimalFormat("#,###.##", new DecimalFormatSymbols(Locale.US));
        df.setDecimalSeparatorAlwaysShown(true);
        df.setRoundingMode(RoundingMode.DOWN);
        df.setMaximumFractionDigits(2);
        dfnd = new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.US));
        this.et = et;
        hasFractionalPart = false;
    }

    @Override
    public void afterTextChanged(Editable s) {
        et.removeTextChangedListener(this);

        try {
            int inilen, endlen;
            inilen = et.getText().length();

            String v = s.toString().replace(String.valueOf(df.getDecimalFormatSymbols().getGroupingSeparator()), "");
            v = v.equals(".") ? "0" : v; // if user enters decimal dot only

            Number n = df.parse(v);
            int cp = et.getSelectionStart();

                if (hasFractionalPart) {
                    et.setText(df.format(n));
                } else {
                    et.setText(dfnd.format(n));
                }

            endlen = et.getText().length();
            int sel = (cp + (endlen - inilen));
            if (sel > 0 && sel <= et.getText().length()) {
                et.setSelection(sel);
            } else {
                // place cursor at the end?
                et.setSelection(et.getText().length() - 1);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 15
                final int finalSel = sel;
                et.postDelayed(() -> {
                    if (!et.isFocused()) {
                        et.requestFocus();
                    }
                    et.setSelection(Math.min(finalSel, et.getText().length()));
                }, 100); // Delay just enough for layout to settle
            } else {
                et.setSelection(sel);
            }

        } catch (NumberFormatException | ParseException nfe) {
            // do nothing?
        }


        et.addTextChangedListener(this);

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        hasFractionalPart = s.toString().contains(String.valueOf(df.getDecimalFormatSymbols().getDecimalSeparator()));
    }
}
