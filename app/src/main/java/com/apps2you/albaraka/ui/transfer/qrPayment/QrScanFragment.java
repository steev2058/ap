package com.apps2you.albaraka.ui.transfer.qrPayment;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.FragmentQrScanBinding;
import com.apps2you.albaraka.ui.base.BaseFragment;
import com.apps2you.albaraka.viewmodels.transfer.QrPaymentVM;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


public class QrScanFragment extends BaseFragment<FragmentQrScanBinding, QrPaymentVM> {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActivityResultLauncher<Intent> activityLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                data -> {
                    //Getting the scan results
                    IntentResult result = IntentIntegrator.parseActivityResult(IntentIntegrator.REQUEST_CODE, data.getResultCode(), data.getData());

                    //if qrCode has nothing in it
                    if (result == null || result.getContents() == null) {
                        showToast(getString(R.string.scan_canceled));
                        requireActivity().finish();
                    } else {
                        //*** check if qrCode is valid
                        getViewModel().checkQrValidity(result.getContents()).observe(this, resource -> {
                            switch (resource.status) {
                                case SUCCESS:
                                    getViewModel().setQrCode(resource.data);
                                    NavHostFragment.findNavController(this).navigate(R.id.action_qrScanFragment_to_qrFormFragment);
                                    break;

                                case ERROR:
                                    showToast(resource.getMessage());
                                    requireActivity().finish();
                            }
                        });
                    }
                });

        activityLauncher.launch(scanCode());
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qr_scan;
    }

    @Override
    public void setUpView() {
    }

    @Override
    public void fetchData() {

    }

    @Override
    public Class<QrPaymentVM> setViewModel() {
        return QrPaymentVM.class;
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    private Intent scanCode() {
        IntentIntegrator integrator = new IntentIntegrator(requireActivity());
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt(getString(R.string.scan_qr_code));
        integrator.setCameraId(0);  // Use a specific camera of the device
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setOrientationLocked(true); // portrait
        return integrator.createScanIntent();
    }
}
