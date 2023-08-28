package com.apps2you.albaraka.ui.transfer.sadaka;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivitySadakaBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.SadakaViewModel;

public class SadakaActivity extends BaseActivity<ActivitySadakaBinding, SadakaViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sadaka;
    }

    @Override
    public Class<SadakaViewModel> setViewModel() {
        return SadakaViewModel.class;
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
