package com.apps2you.albaraka.ui.transfer.payment.mobile;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityMobilePaymentBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.payment.MobilePaymentViewModel;

public class MobilePaymentActivity extends BaseActivity<ActivityMobilePaymentBinding, MobilePaymentViewModel> {
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_mobile_payment;
    }

    @Override
    public Class<MobilePaymentViewModel> setViewModel() {
        return null;
    }

    @Override
    public void setUpView() {

    }

    @Override
    public void fetchData() {

    }

    @Override
    public void listenToVariables() {

    }

    @Override
    protected void setupBaseObservers() {

    }
}
