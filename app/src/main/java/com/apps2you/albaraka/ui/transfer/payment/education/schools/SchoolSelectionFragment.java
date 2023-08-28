package com.apps2you.albaraka.ui.transfer.payment.education.schools;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.SchoolUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.SchoolsPaymentViewModel;

public class SchoolSelectionFragment extends BaseSelectionFragment<SchoolUI, SchoolsPaymentViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.schools);
    }

    @Override
    public Class<SchoolsPaymentViewModel> setViewModel() {
        return SchoolsPaymentViewModel.class;
    }
}
