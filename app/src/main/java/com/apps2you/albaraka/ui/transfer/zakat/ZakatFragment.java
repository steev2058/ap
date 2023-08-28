package com.apps2you.albaraka.ui.transfer.zakat;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentZakatBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.ZakatViewModel;


public class ZakatFragment extends BaseTransferFragment<FragmentZakatBinding, ZakatViewModel> {

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().layoutForm.etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().layoutForm.etAmount));

        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    private void transfer() {
        ZakatForm zakatForm = mViewModel.zakatForm;

        Account selectedFromAccount = mViewModel.selectedAccount.getValue();

        if (selectedFromAccount == null) {
            showToast(R.string.you_must_select_account);
        } else if (!zakatForm.allowed()) {
            showToast(zakatForm.getErrorResource());
            if (zakatForm.isAmountEmpty()) {
                mViewDataBinding.layoutForm.etAmount.requestFocus();
            } else if (zakatForm.isReasonEmpty()) {
                mViewDataBinding.layoutForm.etReason.requestFocus();
            }
        } else if (zakatForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.layoutForm.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(zakatForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(getString(R.string.donate_to_zakat_box));
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
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_zakat;
    }

    @Override
    public Class<ZakatViewModel> setViewModel() {
        return ZakatViewModel.class;
    }
}
