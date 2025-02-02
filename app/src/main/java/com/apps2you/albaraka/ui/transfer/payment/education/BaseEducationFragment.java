package com.apps2you.albaraka.ui.transfer.payment.education;

import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentEducationPaymentBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.payment.education.BaseEducationViewModel;

public abstract class BaseEducationFragment<VM extends BaseEducationViewModel<?>> extends BaseTransferFragment<FragmentEducationPaymentBinding, VM> {

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
        mViewDataBinding.buttonPick.setOnClickListener(view -> navigateToPickFragment());
    }

    protected void transfer() {
        EducationForm educationForm = mViewModel.educationForm;
        if (!educationForm.allowed()) {
            showToast(educationForm.getErrorResource());
        } else if (educationForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(educationForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(mViewDataBinding.getIsSchoolPayment() ? getString(R.string.schools_payment) : getString(R.string.universities_payment),null);
        }
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    public void fetchData() {
        super.fetchData();

        mViewModel.selectedItemLiveData.observe(
                getViewLifecycleOwner(),
                selectableItem -> mViewDataBinding.setSelectedUniversityName(selectableItem.getName())
        );
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_education_payment;
    }


    protected abstract void navigateToPickFragment();
}
