package com.apps2you.albaraka.ui.transfer.hf;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityHfBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.HFViewModel;

public class HFActivity extends BaseActivity<ActivityHfBinding, HFViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_hf;
    }

    @Override
    public Class<HFViewModel> setViewModel() {
        return HFViewModel.class;
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
