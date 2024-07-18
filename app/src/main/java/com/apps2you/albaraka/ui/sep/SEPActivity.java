package com.apps2you.albaraka.ui.sep;

import androidx.fragment.app.FragmentTransaction;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivitySepBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;

public class SEPActivity extends BaseActivity<ActivitySepBinding, SEPViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_sep;
    }

    @Override
    public Class<SEPViewModel> setViewModel() {
        return SEPViewModel.class;
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


    @Override
    public void onBackPressed() {
        // Refresh the SEPFragment on back press

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_sep, new SEPFragment());
        transaction.commit();

            super.onBackPressed(); // Call super to handle default back press behavior

    }
}
