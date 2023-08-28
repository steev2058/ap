package com.apps2you.albaraka.ui.transfer.payment;

import android.os.Bundle;

import androidx.annotation.NavigationRes;
import androidx.annotation.Nullable;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityPaymentBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.base.TransferViewModel;

public abstract class BasePaymentActivity extends BaseActivity<ActivityPaymentBinding, TransferViewModel> {

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
        if (navHostFragment != null) {
            NavController navController = navHostFragment.getNavController();
            navController.setGraph(provideNaveGraphId());
        }
    }

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_payment;
    }

    @Override
    public Class<TransferViewModel> setViewModel() {
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

    protected abstract @NavigationRes
    int provideNaveGraphId();
}
