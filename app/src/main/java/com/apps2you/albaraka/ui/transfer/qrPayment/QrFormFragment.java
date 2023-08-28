package com.apps2you.albaraka.ui.transfer.qrPayment;

import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentQrFormBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferForm;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.viewmodels.transfer.QrPaymentVM;


public class QrFormFragment extends BaseTransferFragment<FragmentQrFormBinding, QrPaymentVM> {

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_qr_form;
    }

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().layoutForm.etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().layoutForm.etAmount));

        // clear it for when user presses "Do another transfer" in TransactionDetails
        getViewModel().form.amount.setValue("");
        getViewModel().form.reason.setValue("");

        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    @Override
    public Class<QrPaymentVM> setViewModel() {
        return QrPaymentVM.class;
    }

    @Override
    protected ViewModelStoreOwner getViewModelOwner() {
        return getActivity();
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

    private void transfer() {
        final BaseTransferForm form = getViewModel().form;

        if (getViewModel().selectedAccount.getValue() == null) {
            showToast(R.string.you_must_select_account);
        } else if (form.isAmountEmpty()) {
            showToast(getString(R.string.error_required));
            getViewDataBinding().layoutForm.etAmount.requestFocus();
        } else if (form.isReasonEmpty()) {
            showToast(getString(R.string.error_required));
            getViewDataBinding().layoutForm.etReason.requestFocus();
        } else if (form.isAmountInvalid()) {
            showToast(getString(R.string.invalid_amount));
            getViewDataBinding().layoutForm.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(form.amount.getValue()))
            showToast(R.string.balance_msg);
        else {
            nextStep(getString(R.string.scan_qr_code));
        }
    }
}
