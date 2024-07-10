package com.apps2you.albaraka.ui.sep.tabs;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.apps2you.albaraka.R;

public class AddPaymentDialogFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_add_payment, container, false);

        Spinner spinnerBiller = view.findViewById(R.id.spinner_billers);
        Spinner spinnerService = view.findViewById(R.id.spinner_billers_services);
//        EditText editInvoiceName = view.findViewById(R.id.edit_invoice_name);
//        EditText editBarcodeNumber = view.findViewById(R.id.edit_barcode_number);
//        EditText editMeterNumber = view.findViewById(R.id.edit_meter_number);
        Button buttonAddToFile = view.findViewById(R.id.button_submit);

        buttonAddToFile.setOnClickListener(v -> {
            // Handle add to file logic
            dismiss();
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
}
