package com.apps2you.albaraka.ui.base.alert;

import android.content.Context;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.apps2you.albaraka.R;

class DialogAlert implements IDialogAlert {

    private final Context context;

    public DialogAlert(Context context) {
        this.context = context;
    }


    /**
     * creates an alert dialog with SeferTasi's themed dialogs.
     * yes_no_dialog will be used if you provide both listeners.
     * dialog_alert will be used if you provide the positive listener only or you don't provide both listeners.
     *
     * @param title                   the title that will be used with bold looking text on top.
     * @param message                 the message that is underneath the title.
     * @param positiveText            dialog_alert will use the positive text when provided, otherwise will use a default ok string.
     *                                dialog_yes_no will use the positive text when provided //todo not implemented
     * @param negativeText            dialog_yes_no will use the negative text when provided //todo not implemented
     * @param onPositiveClickListener provide this listener only to use the dialog_yes_no layout
     * @param onNegativeClickListener provide this listener with the positive to use the dialog_alert layout
     *                                WARNING: DO NOT PASS NEGATIVE WITH NO POSITIVE
     */
    @Override
    public void showDialogMessage(String title, String message,
                                  String positiveText, String negativeText,
                                  OnClickListener onPositiveClickListener,
                                  OnClickListener onNegativeClickListener,
                                  boolean cancelable) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        //inflating the custom view
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_confirm, null);

        builder.setView(view); //attaching the view to the dialog

        builder.setCancelable(cancelable);

        AlertDialog alertDialog = builder.create(); //creating the dialog instance

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     //   alertDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        //TEXTS
        if (title != null) {
            ((TextView) view.findViewById(R.id.textView)).setText(title);
        }
        if (message != null) {
            ((TextView) view.findViewById(R.id.text)).setText(message);
        }

        if (positiveText != null) {
            ((TextView) view.findViewById(R.id.button_submit)).setText(positiveText);
        }

        if (positiveText == null) {
            ((TextView) view.findViewById(R.id.button_submit)).setText(android.R.string.ok);
        }

        //LISTENERS
        view.findViewById(R.id.button_submit).setOnClickListener((v) -> {
            if (onPositiveClickListener != null)
                onPositiveClickListener.onClick(alertDialog, AlertDialog.BUTTON_POSITIVE);
            alertDialog.dismiss();
        });

        view.findViewById(R.id.cancel_button).setOnClickListener((v) -> {
            if (onNegativeClickListener != null)
                onNegativeClickListener.onClick(alertDialog, AlertDialog.BUTTON_NEGATIVE);
            alertDialog.dismiss();
        });

        alertDialog.show();
    }

    @Override
    public void showDialogMessage(String title, String message, String positiveText, String negativeText, OnClickListener onPositiveClickListener, OnClickListener onNegativeClickListener) {
        showDialogMessage(title, message, positiveText, negativeText, onPositiveClickListener, onNegativeClickListener, true);
    }

    @Override
    public void showDialogMessage(String title, String message) {
        showDialogMessage(title, message, null, null, null, null);
    }

    @Override
    public void showDialogMessage(int titleId, int messageId) {
        showDialogMessage(context.getString(titleId), context.getString(messageId));
    }

    @Override
    public void showDialogMessage(String message) {
        showDialogMessage("", message);
    }

    @Override
    public void showDialogMessage(int messageId) {
        showDialogMessage(context.getString(messageId));
    }
}
