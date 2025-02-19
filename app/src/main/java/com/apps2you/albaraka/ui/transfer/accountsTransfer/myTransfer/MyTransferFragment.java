package com.apps2you.albaraka.ui.transfer.accountsTransfer.myTransfer;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.apps2you.albaraka.BR;
import com.apps2you.albaraka.R;
import com.apps2you.albaraka.data.model.Account;
import com.apps2you.albaraka.databinding.FragmentMyTransferBinding;
import com.apps2you.albaraka.ui.base.adapter.OnItemClickListener;
import com.apps2you.albaraka.ui.common.adapters.AccountsRecyclerAdapter;
import com.apps2you.albaraka.ui.transfer.base.BaseTransferFragment;
import com.apps2you.albaraka.utils.NumberTextWatcher;
//import com.apps2you.albaraka.utils.NumberTextWatcher2;
import com.apps2you.albaraka.viewmodels.transfer.MyTransferViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyTransferFragment extends BaseTransferFragment<FragmentMyTransferBinding, MyTransferViewModel> {
    private AccountsRecyclerAdapter toAccountsRecyclerAdapter;

    @Override
    public void setUpView() {
        super.setUpView();
        setUpToAccountsRecycler();

        getViewDataBinding().layoutForm.etAmount.addTextChangedListener(new NumberTextWatcher(getViewDataBinding().layoutForm.etAmount));

        mViewDataBinding.layoutForm.etReason.setVisibility(View.GONE);
        mViewDataBinding.buttonSubmit.setOnClickListener(view -> transfer());
    }

    private void setUpToAccountsRecycler() {
        RecyclerView recyclerView = mViewDataBinding.layoutExpandableToAccountsRecycler.recyclerViewAccounts;
        toAccountsRecyclerAdapter = new AccountsRecyclerAdapter(requireContext(), onToAccountClickListener);
        recyclerView.setAdapter(toAccountsRecyclerAdapter);
    }

    private void transfer() {
        MyTransferForm transferForm = mViewModel.myTransferForm;

        Account selectedFromAccount = mViewModel.selectedAccount.getValue();
        Account selectedToAccount = mViewModel.selectedToAccount.getValue();

        if (selectedFromAccount == null || selectedToAccount == null) {
            showToast(R.string.you_must_select_account);
        } else if (selectedFromAccount.getNumber().equals(selectedToAccount.getNumber())) {
            showToast(R.string.transfer_error);
        } else if (!transferForm.allowed()) {
            showToast(transferForm.getErrorResource());
            if (transferForm.isAmountEmpty()) {
                mViewDataBinding.layoutForm.etAmount.requestFocus();
            } else if (transferForm.isReasonEmpty()) {
                mViewDataBinding.layoutForm.etReason.requestFocus();
            }
        } else if (transferForm.isAmountInvalid()) {
            showToast(R.string.invalid_amount);
            mViewDataBinding.layoutForm.etAmount.requestFocus();
        } else if (!thereIsEnoughBalance(transferForm.amount.getValue())) {
            showToast(R.string.balance_msg);
        } else {
            nextStep(getString(R.string.my_transfers),selectedToAccount);
        }
    }

    private final OnItemClickListener<Account> onToAccountClickListener = (item, position) -> {
        onToAccountSelected(item);
        mViewModel.setSelectedToAccount(item);
    };

    private void onToAccountSelected(Account account) {
        toAccountsRecyclerAdapter.removeItem(account);

        if (mViewModel.selectedToAccount.getValue() != null) {
            toAccountsRecyclerAdapter.insertItem(mViewModel.selectedToAccount.getValue());
        }

        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected void onAccountSelected(Account account) {
        super.onAccountSelected(account);
        mViewDataBinding.motionLayout.transitionToStart();
    }

    @Override
    protected void handleAccountsData(List<Account> accountList) {
        super.handleAccountsData(accountList);

        // remove from the list, the accounts that the user cannot transfer money to
        ArrayList<Account> toAccounts = new ArrayList<>();
        for (Account account : accountList)
            if (account.isAllowTo())
                toAccounts.add(account);

        toAccountsRecyclerAdapter.submitData(toAccounts);
        selectToAccount();
    }

    @Override
    public void refresh() {
        mViewModel.setSelectedToAccount(null);

        super.refresh();
    }

    private void selectToAccount() {
        if (toAccountsRecyclerAdapter.getItemCount() > 0) {
            if (mViewModel.getLastSelectedToAccountId() != null) {
                Account selectedAccount = toAccountsRecyclerAdapter.getData().stream().filter(account -> account.getNumber().equals(mViewModel.getLastSelectedToAccountId()))
                        .findFirst().orElse(toAccountsRecyclerAdapter.getData().get(0));
                onToAccountClickListener.onClick(selectedAccount, toAccountsRecyclerAdapter.getData().indexOf(selectedAccount));
            } else {
                Account selectedAccount = mViewModel.selectedAccount.getValue();
                Account toAccount;
                if (selectedAccount != null) {
                    toAccount =
                            toAccountsRecyclerAdapter
                                    .getData()
                                    .stream().filter(account -> !account.getNumber().equals(selectedAccount.getNumber()))
                                    .findFirst().orElse(toAccountsRecyclerAdapter.getData().get(0));
                } else {
                    toAccount = toAccountsRecyclerAdapter.getData().get(0);
                }
                onToAccountClickListener.onClick(toAccount, toAccountsRecyclerAdapter.getData().indexOf(toAccount));
            }
        }
    }

    @Override
    protected RecyclerView provideAccountsRecycler() {
        return mViewDataBinding.layoutExpandableFromAccountsRecycler.recyclerViewAccounts;
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.fragment_my_transfer;
    }

    @Override
    public Class<MyTransferViewModel> setViewModel() {
        return MyTransferViewModel.class;
    }
}
