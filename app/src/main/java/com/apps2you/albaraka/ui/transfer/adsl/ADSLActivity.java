package com.apps2you.albaraka.ui.transfer.adsl;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityAdslBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.ADSLViewModel;

public class ADSLActivity extends BaseActivity<ActivityAdslBinding, ADSLViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_adsl;
    }

    @Override
    public Class<ADSLViewModel> setViewModel() {
        return ADSLViewModel.class;
    }

    @Override
    public void setUpView() {

    }

    @Override
    public void fetchData() {

    }

    @Override
    protected void setupBaseObservers() {

    }

    @Override
    public void listenToVariables() {

    }
}
