package com.apps2you.albaraka.ui.base.alert;

import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.StringRes;

public interface IDialogAlert {
    void showDialogMessage(String title, String message,
                           String positiveText, String negativeText,
                           DialogInterface.OnClickListener onPositiveClickListener,
                           DialogInterface.OnClickListener onNegativeClickListener,
                           boolean cancelable);

    void showDialogMessage(String title, String message,
                           String positiveText, String negativeText,
                           DialogInterface.OnClickListener onPositiveClickListener,
                           DialogInterface.OnClickListener onNegativeClickListener);

    default void showDialogMessage(String title, String message) {
        showDialogMessage(title, message, null, null, null, null);
    }

    void showDialogMessage(@StringRes int titleId, @StringRes int messageId);

    void showDialogMessage(String message);

    void showDialogMessage(@StringRes int messageId);

    class Factory {
        public static IDialogAlert defaultAlert(Context context) {
            return new DialogAlert(context);
        }
    }
}
