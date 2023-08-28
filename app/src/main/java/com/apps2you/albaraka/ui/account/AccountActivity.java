package com.apps2you.albaraka.ui.account;

import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityAccountBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.AccountVM;


public class AccountActivity extends BaseActivity<ActivityAccountBinding, AccountVM> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_account;
    }

    @Override
    public Class<AccountVM> setViewModel() {
        return AccountVM.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.home_menu_accounts));
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
