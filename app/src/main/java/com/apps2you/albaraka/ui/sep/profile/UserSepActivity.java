package com.apps2you.albaraka.ui.sep.profile;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.databinding.ActivitySepUserBinding;
import com.apps2you.albaraka.ui.base.BaseActivity;
import com.apps2you.albaraka.viewmodels.transfer.SEPViewModel;

public class UserSepActivity extends BaseActivity<ActivitySepUserBinding, SEPViewModel> {
@Override
public int getBindingVariable() {
        return BR.viewModel;
        }

@Override
public int getLayoutId() {
        return R.layout.activity_sep_user;
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
        }