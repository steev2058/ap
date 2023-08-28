package com.apps2you.albaraka.ui.transfer.qrPayment;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityQrPaymentBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.QrPaymentVM;


public class QrPaymentActivity extends BaseActivity<ActivityQrPaymentBinding, QrPaymentVM> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_qr_payment;
    }

    @Override
    public Class<QrPaymentVM> setViewModel() {
        return QrPaymentVM.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.scan_qr_code));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }
}
