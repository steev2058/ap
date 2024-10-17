package com.apps2you.albaraka.ui.sep.bill;

import androidx.fragment.app.FragmentTransaction;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivityBillBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.ui.sep.SEPFragment;
import com.apps2you.albaraka.viewmodels.transfer.BillViewModel;

public class BillActivity extends BaseActivity<ActivityBillBinding, BillViewModel> {
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bill;
    }

    @Override
    public Class<BillViewModel> setViewModel() {
        return BillViewModel.class;
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
//        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_sep);
//        NavController navController = navHostFragment.getNavController();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_bill, new BillFragment());
        transaction.commit();
        //  requireActivity().onBackPressed();

        // Check if there's anything to pop from the back stack
        if (!transaction.isEmpty()) {
            super.onBackPressed(); // Call super to handle default back press behavior
        }
    }
}
