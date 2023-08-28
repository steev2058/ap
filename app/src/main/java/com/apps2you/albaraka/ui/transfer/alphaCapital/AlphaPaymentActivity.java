package com.apps2you.albaraka.ui.transfer.alphaCapital;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityAlphaPaymentBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.base.BaseViewModel;


public class AlphaPaymentActivity extends BaseActivity<ActivityAlphaPaymentBinding, BaseViewModel> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_alpha_payment;
    }

    @Override
    public Class<BaseViewModel> setViewModel() {
        return BaseViewModel.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.alpha_capital_payment));
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
