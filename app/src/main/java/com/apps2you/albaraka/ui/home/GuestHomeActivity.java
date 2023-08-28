package com.apps2you.albaraka.ui.home;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityGuestHomeBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.base.BaseViewModel;

public class GuestHomeActivity extends BaseActivity<ActivityGuestHomeBinding, BaseViewModel> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_guest_home;
    }

    @Override
    public Class<BaseViewModel> setViewModel() {
        return BaseViewModel.class;
    }

    @Override
    public void setUpView() {
     setToolbarTitle(getViewDataBinding().toolbar,"");
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
