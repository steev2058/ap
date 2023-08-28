package com.apps2you.albaraka.ui.transfer.accountsTransfer.sygs;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivitySygsBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.SYGSTransferViewModel;

public class SYGSActivity extends BaseActivity<ActivitySygsBinding, SYGSTransferViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sygs;
    }

    @Override
    public Class<SYGSTransferViewModel> setViewModel() {
        return SYGSTransferViewModel.class;
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
