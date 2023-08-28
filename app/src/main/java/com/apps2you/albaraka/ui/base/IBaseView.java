package com.apps2you.albaraka.ui.base;

import android.content.DialogInterface;

import androidx.annotation.StringRes;

import com.apps2you.albaraka.ui.base.alert.IDialogAlert;

public interface IBaseView {

    default IDialogAlert provideDialogAlert() {
        return null;
    }

    default void showDialogMessage(String title, String message,
                                   String positiveText, String negativeText,
                                   DialogInterface.OnClickListener onPositiveClickListener,
                                   DialogInterface.OnClickListener onNegativeClickListener,
                                   boolean cancelable) {
        if (provideDialogAlert() != null)
            provideDialogAlert().showDialogMessage(title, message, positiveText, negativeText, onPositiveClickListener, onNegativeClickListener, cancelable);
    }

    default void showDialogMessage(String title, String message,
                                   String positiveText, String negativeText,
                                   DialogInterface.OnClickListener onPositiveClickListener,
                                   DialogInterface.OnClickListener onNegativeClickListener) {
        showDialogMessage(title, message, positiveText, negativeText, onPositiveClickListener, onNegativeClickListener, true);
    }

    default void showDialogMessage(String title, String message) {
        showDialogMessage(title, message, null, null, null, null);
    }

    default void showDialogMessage(@StringRes int title, @StringRes int message) {
        if (provideDialogAlert() != null)
            provideDialogAlert().showDialogMessage(title, message);
    }

    default void showDialogMessage(String message) {
        if (provideDialogAlert() != null)
            provideDialogAlert().showDialogMessage(message);
    }

    default void showDialogMessage(@StringRes int message) {
        if (provideDialogAlert() != null)
            provideDialogAlert().showDialogMessage(message);
    }

    void showToast(final String message);
    void showToast(@StringRes final int stringRes);
    void hideKeyboard();
}
