package com.apps2you.albaraka.ui.transfer.payment.education.schools;

import com.apps2you.albaraka.R;
import com.apps2you.albaraka.ui.transfer.payment.education.BaseEducationFragment;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.SchoolsPaymentViewModel;

public class SchoolsPaymentFragment extends BaseEducationFragment<SchoolsPaymentViewModel> {

    @Override
    protected void transfer() {
        if (mViewModel.getSelectedItem() == null){
            showToast(R.string.you_must_select_school);
        }else {
            super.transfer();
        }
    }

    @Override
    public void setUpView() {
        super.setUpView();
        mViewDataBinding.setIsSchoolPayment(true);
    }

    @Override
    protected void navigateToPickFragment() {
        navController.navigate(
                SchoolsPaymentFragmentDirections
                        .actionSchoolsPaymentFragmentToSchoolSelectionFragment()
        );
    }

    @Override
    public Class<SchoolsPaymentViewModel> setViewModel() {
        return SchoolsPaymentViewModel.class;
    }
}
