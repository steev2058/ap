package com.apps2you.albaraka.ui.transfer.payment.mobile;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.data.model.LineType;
import com.apps2you.albaraka.data.model.PaymentCategory;
import com.apps2you.albaraka.databinding.FragmentMobilePaymentBinding;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
import com.apps2you.albaraka.utils.lifecyle.EventObserver;
import com.apps2you.albaraka.viewmodels.transfer.payment.MobilePaymentViewModel;

public class MobilePaymentFragment extends BaseTransferFragment<FragmentMobilePaymentBinding, MobilePaymentViewModel> {

    private OperatorTypeTextWatcher operatorChangeListener;

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    public void setUpView() {
        super.setUpView();

        getViewDataBinding().etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().etAmount));

        mViewDataBinding.buttonPickContact.setOnClickListener(view -> pickContact());

        operatorChangeListener = new OperatorTypeTextWatcher(mViewModel::selectOperator);
        mViewDataBinding.etGsmNumber.addTextChangedListener(operatorChangeListener);

        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());

        setupLineTypeSpinner();
        setupPaymentCategorySpinner();
    }

    private void transfer() {
        MobilePaymentForm form = mViewModel.form;

        if (mViewModel.selectedAccount.getValue() == null) {
            showToast(R.string.you_must_select_account);
        } else if (!form.allowed()) {
            showToast(form.getErrorResource());
        } else if (mViewModel.selectedOperator.getValue() == null
                || mViewModel.selectedLineType.getValue() == null
                || mViewModel.selectedPaymentCategory.getValue() == null && !mViewModel.isPostpaid()
                || TextUtils.isEmpty(form.amount.getValue()) && mViewModel.isPostpaid()) {

            showToast(R.string.error_fields_required);

        } else if (mViewModel.isPostpaid() && form.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(form.amount.getValue()) && mViewModel.isPostpaid()
                || !mViewModel.isPostpaid() && !thereIsEnoughBalance(mViewModel.selectedPaymentCategory.getValue().getTotalCost().toString())) {

            showToast(R.string.balance_msg);

        } else if (!thereIsEnoughBalance(form.amount.getValue(), mViewModel.selectedOperator.getValue().getMaxLimit()) && mViewModel.isPostpaid()) {
            showToast(getString(R.string.maximum_msg)
                    .concat(" ")
                    .concat(mViewModel.selectedOperator.getValue().getMaxLimit().toString()));
        } else {
            openTransferPreviewDialog();
        }
    }

    private void openTransferPreviewDialog() {
        TransferPreviewDialog.show(getChildFragmentManager());
    }

    private void setupLineTypeSpinner() {
        mViewDataBinding.spinnerLineType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mViewModel.selectLineType((LineType) mViewDataBinding.spinnerLineType.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupPaymentCategorySpinner() {
        mViewDataBinding.spinnerPaymentCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mViewModel.selectPaymentCategory((PaymentCategory) mViewDataBinding.spinnerPaymentCategory.getSelectedItem());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    protected void onContactSelected(String number) {
        if (number != null) {
            mViewModel.form.gsmNumber.setValue(number);
        }
    }

    @Override
    public void fetchData() {
        super.fetchData();

        mViewModel.operatorList.observe(getViewLifecycleOwner(), operatorChangeListener::setOperators);

        mViewModel.paymentConfirmed.observe(getViewLifecycleOwner(),
                new EventObserver<>(
                        confirmed -> {
                            if (confirmed)
                                openConfirmPinDialog();
                        }
                )
        );
    }

    @Override
    public void refresh() {
        super.refresh();
        mViewModel.fetchOperators();
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
        return R.layout.fragment_mobile_payment;
    }

    @Override
    public Class<MobilePaymentViewModel> setViewModel() {
        return MobilePaymentViewModel.class;
    }
}
