package com.apps2you.albaraka.utils.text;

import java.text.NumberFormat;
import java.util.Locale;

public class TextUtils {
    public static boolean isValidPhoneNumber(String phoneNumber) {
        if (android.text.TextUtils.isEmpty(phoneNumber)) {
            return false;
        }
        return phoneNumber.length() == 9 && phoneNumber.startsWith("9");
    }

    public static String withoutCountryCode(String phoneNumber) {
        if (phoneNumber == null) return null;
        String regex = "^(\\+963|00963|963|0)";
        return phoneNumber.replaceAll("\\s+", "") // one or more spaces
                .replace("-", "")
                .replace(")", "")
                .replace("(", "")
                .replaceFirst(regex, "");
    }

    public static String toEnglishNumber(String number) {
        NumberFormat formatter = NumberFormat.getInstance(Locale.ENGLISH);

        return replaceNonstandardDigits(number.replace(",", "")); // remove grouping separator
    }

    public static String toEnglishLocale(String text) {
        return replaceNonstandardDigits(text);
    }

    static String replaceNonstandardDigits(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); i++) {
            char ch = input.charAt(i);
            if (Character.isDigit(ch) && !(ch >= '0' && ch <= '9')) {
                int numericValue = Character.getNumericValue(ch);
                if (numericValue >= 0) {
                    builder.append(numericValue);
                }
            } else {
                builder.append(ch);
            }
        }
        return builder.toString();
    }
}
