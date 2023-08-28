package com.apps2you.albaraka.ui.transfer.payment.education.universities;

import androidx.lifecycle.ViewModelStoreOwner;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.common.model.UniversityUI;
import com.apps2you.albaraka.ui.transfer.base.BaseSelectionFragment;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.UniversitiesPaymentViewModel;

public class UniversitySelectionFragment extends BaseSelectionFragment<UniversityUI, UniversitiesPaymentViewModel> {

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    protected String provideTitle() {
        return getString(R.string.universities);
    }

    @Override
    public Class<UniversitiesPaymentViewModel> setViewModel() {
        return UniversitiesPaymentViewModel.class;
    }
}
