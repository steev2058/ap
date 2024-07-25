package com.apps2you.albaraka.ui.sep;

import android.content.Intent;

import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

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
    public void refreshActivity() {
        Intent intent = new Intent(this, SEPActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);

        finish();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
    }


}
