package com.apps2you.albaraka.ui.common.dialogs;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.DialogDataRetrievalErrorBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.BaseFragmentDialog;
import com.apps2you.albaraka.ui.common.busEvent.DismissRequestErrorEvent;
import com.apps2you.albaraka.utils.bus.Bus;

import org.jetbrains.annotations.NotNull;


public class DataRetrievalErrorDialog extends BaseFragmentDialog<DialogDataRetrievalErrorBinding> {
    public static final String TAG_ERROR_DIALOG = "error_dialog";

    private final Exception emittedException;
    private final View.OnClickListener onRetryClickedListener;

    private boolean retryClicked;

    private DataRetrievalErrorDialog(Exception emittedException,
                                     View.OnClickListener onRetryClickedListener) {
        this.emittedException = emittedException;
        this.onRetryClickedListener = onRetryClickedListener;
    }

    public static DataRetrievalErrorDialog create(Exception emittedException,
                                                  View.OnClickListener onRetryClickedListener) {
        return new DataRetrievalErrorDialog(emittedException, onRetryClickedListener);
    }

    public static void show(
            Exception emittedException,
            View.OnClickListener onRetryClickedListener,
            FragmentManager fragmentManager
    ) {
        create(emittedException, onRetryClickedListener).show(fragmentManager, TAG_ERROR_DIALOG);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setCanceledOnTouchOutside(false);

        binding.setEmittedException(emittedException);

        binding.buttonRetry.setOnClickListener(v -> {
            retryClicked = true;
            dismiss();
            if (onRetryClickedListener != null) {
                onRetryClickedListener.onClick(v);
            }
        });

    }

    @Override
    public void onDismiss(@NonNull @NotNull DialogInterface dialog) {
        super.onDismiss(dialog);

        if (!retryClicked) {
            Bus.instance().publish(new DismissRequestErrorEvent(requireActivity(), emittedException));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_data_retrieval_error;
    }
}
