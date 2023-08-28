package com.apps2you.albaraka.ui.transfer.alphaCapital;

import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentAlphaPaymentBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.AlphaPaymentVM;


public class AlphaPaymentFragment extends BaseTransferFragment<FragmentAlphaPaymentBinding, AlphaPaymentVM> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_alpha_payment;
    }

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        // clear it for when user presses "Do another transfer" in TransactionDetails
        getViewModel().setAmount("");
        getViewDataBinding().etAmount.setText("");

        getViewModel().setAlphaId("");
        getViewDataBinding().etId.setText("");


        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    @Override
    public Class<AlphaPaymentVM> setViewModel() {
        return AlphaPaymentVM.class;
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

    private void transfer() {

        if (getViewModel().selectedAccount.getValue() == null) {
            showToast(R.string.you_must_select_account);
        } else if (TextUtils.isEmpty(getViewModel().getAlphaId())) {
            showToast(getString(R.string.error_required));
            getViewDataBinding().etId.requestFocus();
        } else if (TextUtils.isEmpty(getViewModel().getAmount())) {
            showToast(getString(R.string.error_required));
            getViewDataBinding().etAmount.requestFocus();
        } else if (getViewModel().isAmountInvalid()) {
            showToast(getString(R.string.invalid_amount));
            getViewDataBinding().etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(getViewModel().getAmount()))
            showToast(R.string.balance_msg);
        else {
            nextStep(getString(R.string.alpha_capital_payment));
        }
    }
}
