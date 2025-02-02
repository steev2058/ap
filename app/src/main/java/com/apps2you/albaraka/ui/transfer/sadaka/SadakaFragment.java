package com.apps2you.albaraka.ui.transfer.sadaka;

import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentSadakaBinding;
import com.apps2you.albaraka.ui.common.model.CharityUI;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.SadakaViewModel;

public class SadakaFragment extends BaseTransferFragment<FragmentSadakaBinding, SadakaViewModel> {

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonPickCharity.setOnClickListener(view -> openCharitiesFragment());
        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    private void openCharitiesFragment() {
        navController.navigate(
                SadakaFragmentDirections.actionSadakaFragmentToCharitiesFragment()
        );
    }

    public void transfer() {
        SadakaForm sadakaForm = mViewModel.sadakaForm;

        Account selectedAccount = mViewModel.selectedAccount.getValue();
        CharityUI selectedCharity = mViewModel.getSelectedItem();

        if (selectedAccount == null) {
            showToast(R.string.you_must_select_account);
        } else if (selectedCharity == null) {
            showToast(R.string.you_must_select_charity);
        } else if (!sadakaForm.allowed()) {
            showToast(sadakaForm.getErrorResource());
            if (sadakaForm.isAmountEmpty()) {
                mViewDataBinding.etAmount.requestFocus();
            } else if (sadakaForm.isPhoneEmpty()) {
                mViewDataBinding.etPhone.requestFocus();
            } else if (sadakaForm.isReasonEmpty()) {
                mViewDataBinding.etReason.requestFocus();
            }
        } else if (sadakaForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(sadakaForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(getString(R.string.donate_to_charities),null);
        }
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mViewModel.stopContentLoading();
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
        return R.layout.fragment_sadaka;
    }

    @Override
    public Class<SadakaViewModel> setViewModel() {
        return SadakaViewModel.class;
    }

}
