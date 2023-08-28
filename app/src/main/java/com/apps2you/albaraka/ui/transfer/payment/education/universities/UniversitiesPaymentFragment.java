package com.apps2you.albaraka.ui.transfer.payment.education.universities;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.transfer.payment.education.BaseEducationFragment;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.UniversitiesPaymentViewModel;

public class UniversitiesPaymentFragment extends BaseEducationFragment<UniversitiesPaymentViewModel> {

    @Override
    protected void transfer() {
        if (mViewModel.getSelectedItem() == null){
            showToast(R.string.you_must_select_university);
        }else {
            super.transfer();
        }
    }

    @Override
    protected void navigateToPickFragment() {
        navController.navigate(
                UniversitiesPaymentFragmentDirections
                        .actionUniversitiesPaymentFragmentToUniversitySelectionFragment()
        );
    }

    @Override
    public Class<UniversitiesPaymentViewModel> setViewModel() {
        return UniversitiesPaymentViewModel.class;
    }
}
