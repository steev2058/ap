package com.apps2you.albaraka.ui.common.dialogs;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.MyApplication;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.User;
import com.apps2you.albaraka.data.preference.UserUtils;
import com.apps2you.albaraka.databinding.DialogConfirmPinBinding;
import com.apps2you.albaraka.ui.base.dialogfragment.MVVMFragmentDialog;
import com.apps2you.albaraka.utils.AsteriskPasswordTransformationMethod;
import com.apps2you.albaraka.utils.Constants;
import com.apps2you.albaraka.viewmodels.ConfirmPinViewModel;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class ConfirmPinDialog extends MVVMFragmentDialog<ConfirmPinViewModel, DialogConfirmPinBinding> {
    public static final String TAG_CONFIRM_PIN_DIALOG = "confirm_pin_dialog";

    private final PinConfirmationListener pinConfirmationListener;
    private static int transferTypeId = -1;
    private SweetAlertDialog progressDialog;

    public static void setTransferTypeId(int transferTypeId2) {
        transferTypeId = transferTypeId2;
    }

    private ConfirmPinDialog(PinConfirmationListener pinConfirmationListener) {
        this.pinConfirmationListener = pinConfirmationListener;

    }

    public static ConfirmPinDialog create(PinConfirmationListener pinConfirmationListener) {
        return new ConfirmPinDialog(pinConfirmationListener);
    }

    public static void show(FragmentManager fragmentManager, PinConfirmationListener pinConfirmationListener) {
        create(pinConfirmationListener).show(fragmentManager, TAG_CONFIRM_PIN_DIALOG);
    }
    User user = UserUtils.getInstance(MyApplication.getAppContext()).getUser();
    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setCanceledOnTouchOutside(false);
        registerObservers();
        progressDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
        //progressDialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        progressDialog.getProgressHelper().setBarColor(getResources().getColor(R.color.colorAccent));
        progressDialog.setContentText(getString(R.string.loading));
        progressDialog.setCancelable(false);

        // Set the text dynamically based on TransferTypeId
        if (transferTypeId == Constants.TRANSFER_AL_BARAKA  && user.getEnableOtp() == 1) {
            binding.tvEnterPinCode.setText(R.string.enter_otp);
            binding.buttonResend.setVisibility(View.VISIBLE); // Show the "Resend" button
            binding.tvTimer.setVisibility(View.VISIBLE);
        } else {
            binding.tvEnterPinCode.setText(R.string.pin_code_confirm);
            binding.buttonResend.setVisibility(View.GONE); // Hide the "Resend" button
            binding.tvTimer.setVisibility(View.GONE);
        }


        binding.pinView.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        binding.pinView.requestFocus();
        showKeyBoard();



        viewModel.getTimerText().observe(getViewLifecycleOwner(), text -> {
            binding.tvTimer.setText(text);
        });

        viewModel.getIsResendEnabled().observe(getViewLifecycleOwner(), isEnabled -> {
            binding.buttonResend.setEnabled(isEnabled);
            // Dynamically change the button style
            int style = isEnabled ? R.drawable.bg_shadow_orange : R.drawable.bg_shadow_gray;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.buttonResend.setBackgroundResource(style);
            }
        });
        String resendAvailableWithTime = getString(R.string.resend_available_with_time);
        String resendAvailableWithNoTime = getString(R.string.resend_available_no_time);
        String resendEnabled = getString(R.string.resend_enabled);
        binding.buttonResend.setOnClickListener(v -> {
            if (Boolean.TRUE.equals(viewModel.getIsResendEnabled().getValue())) {
                resendOtp(); // Resend OTP
                // Reset timer
                viewModel.resetTimer(resendAvailableWithTime, resendAvailableWithNoTime, resendEnabled);
            }
        });

        // Start the timer initially if needed
       // viewModel.startTimer();
        viewModel.startTimer(resendAvailableWithTime, resendAvailableWithNoTime, resendEnabled);

        //binding.buttonResend.setOnClickListener(v -> resendOtp());
    }

    private void registerObservers() {
        viewModel.pinCodeError.observe(getViewLifecycleOwner(), result -> {
            if (result)
                shakeError();
        });

        viewModel.checkPinStatus.observe(getViewLifecycleOwner(), result -> {
            if (result) {
                pinConfirmationListener.onPinConfirmed(viewModel.pinCode.getValue());
                dismiss();
            }
        });
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.stopTimer(); // Stop the timer to avoid memory leaks
    }
    private void shakeError() {
        Animation shake = AnimationUtils.loadAnimation(requireContext(), R.anim.shake);
        binding.pinView.startAnimation(shake);
    }
    private void resendOtp() {
        // Run the network operation on a background thread
        Executors.newSingleThreadExecutor().execute(() -> {
            String response = sendOtp(user.getCif_number());
            // Update the UI on the main thread
            requireActivity().runOnUiThread(() -> {
                if (response != null) {
                    Toast.makeText(requireContext(), getString(R.string.notif_otp), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    protected void showProgress() {
        progressDialog.show();
    }
    protected void hideProgress() {
        progressDialog.dismiss();
    }
    private String sendOtp(String... params) {
        String cifNumber = params[0];
        HttpURLConnection connection = null;

        try {
            URL url = new URL(Constants.BASE_URL + "/api/send_otp");
            connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            Map<String, String> postData = new HashMap<>();
            postData.put("cif", cifNumber);

            StringBuilder requestBody = new StringBuilder();
            for (Map.Entry<String, String> entry : postData.entrySet()) {
                if (requestBody.length() > 0) {
                    requestBody.append("&");
                }
                requestBody.append(URLEncoder.encode(entry.getKey(), "UTF-8"))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            }

            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBody.toString().getBytes("UTF-8"));
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                StringBuilder response = new StringBuilder();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        response.append(line);
                    }
                }
                return response.toString();
            } else {
                Log.e("sendOtp", "HTTP error code: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }


    @Override
    protected Class<ConfirmPinViewModel> getViewModelClass() {
        return ConfirmPinViewModel.class;
    }

    @Override
    protected int getViewModelId() {
        return BR.viewModel;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_confirm_pin;
    }

    public interface PinConfirmationListener {
        void onPinConfirmed(String pinCode);
    }
}
