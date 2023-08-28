package com.apps2you.albaraka.ui.calculator;

import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityCalculatorBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.base.BaseViewModel;


public class CalculatorActivity extends BaseActivity<ActivityCalculatorBinding, BaseViewModel> {

    @Override
    public int getBindingVariable() {
        return 0;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_calculator;
    }

    @Override
    public Class<BaseViewModel> setViewModel() {
        return BaseViewModel.class;
    }

    @Override
    public void setUpView() {
        setToolbarTitle(getViewDataBinding().toolbar, getString(R.string.profits_and_deposit_calculate));

        getViewDataBinding().btnFinancing.setChecked(true); // checked by default // financingFragment is the start destination
        getViewDataBinding().radioSub.setVisibility(View.GONE);

        getViewDataBinding().btnFinancing.setOnCheckedChangeListener((buttonView, isChecked) -> {
            NavController navController = Navigation.findNavController(this, R.id.fragment);
            navController.popBackStack();

            if (isChecked) {
                getViewDataBinding().radioSub.setVisibility(View.GONE);
                getViewDataBinding().radioSub.clearCheck();
                navController.navigate(R.id.financingFragment);
            }
        });

        getViewDataBinding().btnProfits.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                getViewDataBinding().radioSub.setVisibility(View.VISIBLE);
                getViewDataBinding().btnProfitsCal.setChecked(true);
                getViewDataBinding().radioSub.check(R.id.btn_profits_cal);
            }
        });

        getViewDataBinding().btnProfitsCal.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                NavController navController = Navigation.findNavController(this, R.id.fragment);
                navController.popBackStack();
                navController.navigate(R.id.profitsFragment);
            }
        });


//        getViewDataBinding().btnDepositCal.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) {
//                NavController navController = Navigation.findNavController(this, R.id.fragment);
//                navController.popBackStack();
//                navController.navigate(R.id.depositFragment);
//            }
//        });
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
