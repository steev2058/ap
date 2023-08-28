package com.apps2you.albaraka.ui.transfer.zakat;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityZakatBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.ZakatViewModel;

public class ZakatActivity extends BaseActivity<ActivityZakatBinding, ZakatViewModel> {
    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_zakat;
    }

    @Override
    public Class<ZakatViewModel> setViewModel() {
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
